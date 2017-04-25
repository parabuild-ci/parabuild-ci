/*
 * @(#)VolatileTransfer.java
 *
 */
package rath.msnm.ftp;

public interface VolatileTransfer
{
	/**
	 * 실제로 송/수신된 바이트 크기를 얻어온다.
	 */
	public int getCommitPercent();

	/**
	 * 송/수신되고 있는 파일의 이름을 얻어온다.
	 */
	public String getFilename();
}
