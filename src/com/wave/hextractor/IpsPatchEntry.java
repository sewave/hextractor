package com.wave.hextractor;

public class IpsPatchEntry {
	public static final int IPS_OFFSET_SIZE = 3;
	public static final int IPS_DATA_SIZE = 2;
	public static final int IPS_RLE_DATA_SIZE = 2;
	public static final int IPS_RLE_DATA_BIN_SIZE = 2;
	public static final int IPS_RLE_MODE = 0;
	public static final int IPS_CHUNK_MIN_SIZE = IPS_OFFSET_SIZE + IPS_DATA_SIZE + 1;

	int offset;
	int size;
	int rleSize;
	byte[] data;


	public int getBinSize() {
		int binSize = IPS_OFFSET_SIZE + IPS_DATA_SIZE;
		if(size == IPS_RLE_MODE) {
			//Add rle size
			binSize += IPS_RLE_DATA_SIZE;
		}
		binSize += data.length;
		return binSize;
	}

	public byte[] toBin() {
		byte[] binEntry = new byte[getBinSize()];
		//Append OFFSET - DATA_SIZE (- RLE_DATA_SIZE) - DATA (1-N)
		int dataOff = 0;
		byte[] offsetAr = Utils.intToByteArray(offset);
		binEntry[dataOff++] = offsetAr[1];
		binEntry[dataOff++] = offsetAr[2];
		binEntry[dataOff++] = offsetAr[3];
		byte[] sizeAr = Utils.intToByteArray(size);
		binEntry[dataOff++] = sizeAr[2];
		binEntry[dataOff++] = sizeAr[3];
		if(size == IPS_RLE_MODE) {
			//RLE entry
			byte[] rleSizeAr = Utils.intToByteArray(rleSize);
			binEntry[dataOff++] = rleSizeAr[2];
			binEntry[dataOff++] = rleSizeAr[3];
			binEntry[dataOff++] = data[0];
		}
		else {
			System.arraycopy(data, 0, binEntry, dataOff, data.length);
		}
		return binEntry;
	}

	public IpsPatchEntry() {
		super();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		int sizeData;
		if(this.size == IPS_RLE_MODE) {
			sb.append("RLE ");
			sizeData = this.getRleSize();
		}
		else {
			sb.append("PAT ");
			sizeData = this.getSize();
		}
		sb.append("[offset=").append(Utils.getHexFilledLeft(offset, Constants.HEX_ADDR_SIZE)).
		append(", size=").append(Utils.getHexFilledLeft(sizeData, Constants.HEX_ADDR_SIZE)).append("]");
		return sb.toString();
	}

	public IpsPatchEntry(int offset, int size, byte[] data) {
		super();
		this.offset = offset;
		this.size = size;
		this.data = data;
	}

	public IpsPatchEntry(int offset, int size, int rleSize, byte[] data) {
		super();
		this.offset = offset;
		this.size = size;
		this.rleSize = rleSize;
		this.data = data;
	}

	public int getRleSize() {
		return rleSize;
	}

	public void setRleSize(int rleSize) {
		this.rleSize = rleSize;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public IpsPatchEntry(int offset, int size) {
		this.offset = offset;
		this.size = size;
	}

	public IpsPatchEntry(int offset) {
		this.offset = offset;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
}
