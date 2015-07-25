package com.kc.sketchrobot.service;

class ChannelValues {

	private static final int CMD = 0;
	private static final int VAL = 1;

	// 8 channels for commands
	private int[] channel = new int[8];
	private StringBuffer sb = new StringBuffer();

	public void setValue(int index, int value) {
		channel[index] = value;
	}

	public String toString() {
		sb.setLength(0);
		for (int i = 0; i < channel.length; i++) {
			int value = channel[i];

			switch (i) {
			case VAL:
				
				break;

			case CMD:

				break;

			default:
			
			}
			
			sb.append(formatNumber(value));
		}

		String s = sb.toString();
		byte[] bytes = s.getBytes();
		int checkSum = 0;
		for (Byte b : bytes) {
			checkSum += (b.intValue() - 48);
		}

		s += formatNumber(checkSum);

		return s;
	}

	public byte[] toBytes() {
		return (toString() + ";").getBytes();
	}

	public int getValue(int channelId) {
		return channel[channelId];
	}

	private String formatNumber(Integer num) {
		if (num < 10) {
			return "00" + num.toString();
		}
		if (num < 100) {
			return "0" + num.toString();
		}

		return num.toString();

	}

}
