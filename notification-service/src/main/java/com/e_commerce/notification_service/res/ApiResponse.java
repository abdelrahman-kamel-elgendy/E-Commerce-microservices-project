package com.e_commerce.notification_service.res;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ApiResponse<T> {
    public boolean success;
    public String message;
    public T data;
}