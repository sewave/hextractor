package com.wave.hextractor.pojo;

/**
 * File with digests.
 * @author slcantero
 */
public class FileWithDigests {
	private String name;
	private byte[] bytes;
	private String md5;
	private String sha1;
	private String crc32;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public byte[] getBytes() {
		return bytes;
	}
	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}
	public String getMd5() {
		return md5;
	}
	public void setMd5(String md5) {
		this.md5 = md5;
	}
	public String getSha1() {
		return sha1;
	}
	public void setSha1(String sha1) {
		this.sha1 = sha1;
	}
	public String getCrc32() {
		return crc32;
	}
	public void setCrc32(String crc32) {
		this.crc32 = crc32;
	}
	
}
