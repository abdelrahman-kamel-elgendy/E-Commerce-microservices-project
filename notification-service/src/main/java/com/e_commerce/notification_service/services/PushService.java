package com.e_commerce.notification_service.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.e_commerce.notification_service.dto.NotificationResponse;
import com.e_commerce.notification_service.dto.PushRequest;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import jakarta.annotation.PostConstruct;
import com.e_commerce.notification_service.exceptions.ProviderInitializationException;
import com.e_commerce.notification_service.exceptions.ProviderSendException;

@Service
public class PushService {

	@Value("${push.enabled:false}")
	private boolean pushEnabled;

	@Value("${fcm.enabled:false}")
	private boolean fcmEnabled;

	@Value("${fcm.credentials.path:}")
	private String fcmCredentialsPath;

	@PostConstruct
	public void init() {
		if (pushEnabled && fcmEnabled && FirebaseApp.getApps().isEmpty() && fcmCredentialsPath != null
				&& !fcmCredentialsPath.isBlank()) {
			try (java.io.FileInputStream in = new java.io.FileInputStream(fcmCredentialsPath)) {
				FirebaseOptions options = FirebaseOptions.builder()
						.setCredentials(com.google.auth.oauth2.GoogleCredentials.fromStream(in))
						.build();
				FirebaseApp.initializeApp(options);
			} catch (Exception e) {
				throw new ProviderInitializationException("Failed to initialize Firebase", e);
			}
		}
	}

	public NotificationResponse sendPush(PushRequest request) {
		if (!pushEnabled) {
			return new NotificationResponse(true, "Push disabled - skipped", null);
		}

		if (!fcmEnabled) {
			return new NotificationResponse(true, "FCM disabled - skipped", null);
		}

		Notification notif = Notification.builder().setTitle(request.getTitle()).setBody(request.getBody()).build();
		AndroidConfig android = AndroidConfig.builder().build();
		ApnsConfig apns = ApnsConfig.builder().setAps(Aps.builder().setContentAvailable(true).build()).build();

		Message message;
		if (request.getTokenOrTopic().startsWith("/topics/")) {
			message = Message.builder().setTopic(request.getTokenOrTopic().substring("/topics/".length()))
					.setNotification(notif).setAndroidConfig(android).setApnsConfig(apns).build();
		} else {
			message = Message.builder().setToken(request.getTokenOrTopic()).setNotification(notif).setAndroidConfig(android)
					.setApnsConfig(apns).build();
		}

		try {
			String id = FirebaseMessaging.getInstance().send(message);
			return new NotificationResponse(true, "Push sent", id);
		} catch (Exception e) {
			throw new ProviderSendException("Failed to send push notification", e);
		}
	}
}


