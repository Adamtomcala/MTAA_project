package com.example.vis;

interface OnFinishLoginListener {
    void onSuccess_login();
    void onFailed_login();
    void onFailed_loginExists();
}