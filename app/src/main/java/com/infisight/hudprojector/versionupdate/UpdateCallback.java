package com.infisight.hudprojector.versionupdate;

public interface UpdateCallback {
	public void checkUpdateCompleted(Boolean hasUpdate, CharSequence updateInfo);

	public void downloadProgressChanged(int progress);

	public void downloadCanceled();

	public void downloadCompleted(Boolean sucess, CharSequence errorMsg);
}
