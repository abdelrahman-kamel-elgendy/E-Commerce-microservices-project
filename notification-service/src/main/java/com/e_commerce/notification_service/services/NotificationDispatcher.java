package com.e_commerce.notification_service.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import com.e_commerce.notification_service.dto.DispatchRequest;
import com.e_commerce.notification_service.dto.NotificationResponse;
import com.e_commerce.notification_service.dto.PushRequest;
import com.e_commerce.notification_service.models.ChannelType;

@Service
public class NotificationDispatcher {

	@Autowired
	private NotificationService emailFacade;

	@Autowired
	private EmailService emailService;


	@Autowired
	private PushService pushService;

	@Retryable(maxAttempts = 3, backoff = @Backoff(delay = 500, multiplier = 2))
	public NotificationResponse dispatch(DispatchRequest req) {
		switch (req.getChannel()) {
			case EMAIL:
				return dispatchEmail(req);
			case PUSH:
				return pushService.sendPush(new PushRequest(req.getTo(),
						req.getVariables() != null ? String.valueOf(req.getVariables().getOrDefault("title", "Notification"))
								: "Notification",
						req.getVariables() != null ? String.valueOf(req.getVariables().getOrDefault("body", "")) : ""));
			case IN_APP:
				// In-app handled through dedicated controller/service
				return new NotificationResponse(true, "In-app dispatch handled via dedicated endpoints", null);
			default:
				return new NotificationResponse(false, "Unsupported channel", null);
		}
	}

	private NotificationResponse dispatchEmail(DispatchRequest req) {
		// Map generic request into existing typed flows
		switch (req.getType()) {
			case WELCOME:
				return emailFacade.sendWelcomeEmail(new com.e_commerce.notification_service.dto.WelcomeEmailRequest(req.getTo(),
						(String) (req.getVariables() != null ? req.getVariables().getOrDefault("userName", "") : "")));
			case PASSWORD_RESET:
				return emailFacade.sendPasswordResetEmail(new com.e_commerce.notification_service.dto.PasswordResetEmailRequest(req.getTo(),
						(String) (req.getVariables() != null ? req.getVariables().getOrDefault("userName", "") : ""),
						(String) (req.getVariables() != null ? req.getVariables().getOrDefault("resetUrl", "") : ""),
						(String) (req.getVariables() != null ? req.getVariables().getOrDefault("token", "") : "")));
			case ACCOUNT_LOCKED:
				return emailFacade.sendAccountLockedEmail(new com.e_commerce.notification_service.dto.AccountLockedEmailRequest(req.getTo(),
						(String) (req.getVariables() != null ? req.getVariables().getOrDefault("userName", "") : ""),
						(String) (req.getVariables() != null ? req.getVariables().getOrDefault("unlockUrl", "") : "")));
			case EMAIL_VERIFICATION:
				return emailFacade.sendEmailVerificationEmail(req.getTo(),
						(String) (req.getVariables() != null ? req.getVariables().getOrDefault("userName", "") : ""),
						(String) (req.getVariables() != null ? req.getVariables().getOrDefault("verificationUrl", "") : ""));
			case GENERIC:
			default:
				// Generic email via template
				com.e_commerce.notification_service.dto.EmailRequest email = new com.e_commerce.notification_service.dto.EmailRequest(
						req.getTo(),
						(String) (req.getVariables() != null ? req.getVariables().getOrDefault("subject", "Notification") : "Notification"),
						req.getTemplateName() != null ? req.getTemplateName() : "welcome-email",
						req.getVariables());
				emailService.sendEmailAsync(email);
				return new NotificationResponse(true, "Email queued", "EMAIL_" + System.currentTimeMillis());
		}
	}
}


