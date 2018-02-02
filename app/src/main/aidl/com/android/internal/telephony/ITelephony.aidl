package com.android.internal.telephony;
interface ITelephony{
boolean endCall();
void answerRingingCall();
boolean isRadioOn();
boolean isDataConnectivityPossible();
boolean setRadio(boolean turnOn);
boolean enableDataConnectivity();
void cancelMissedCallsNotification();
boolean disableDataConnectivity();
void silenceRinger();
boolean showCallScreen();
void dial(String number);
}