package com.mti.rfid.minime;

public class CMD_ModConf {
	
	public enum Region {
		US_CA((byte)0),
		EU((byte)1),
		TW((byte)2),
		CN((byte)3),
		KR((byte)4),
		AU_NZ((byte)5),
		EU2((byte)6),
		BR((byte)7),
		HK((byte)8),
		MY((byte)9),
		SG((byte)10),
		TH((byte)11),
		IL((byte)12),
		RU((byte)13),
		IN((byte)14),
		SA((byte)15),
		JO((byte)16),
		MX((byte)17);
		
		private byte bRegion;
		
		Region(byte bRegion) {
			this.bRegion = bRegion;
		}
	}


	/************************************************************
	 **					RFID_RadioSetRegion						*
	 ************************************************************/
	static final class RFID_RadioSetRegion extends MtiCmd {
		public RFID_RadioSetRegion(UsbCommunication usbComm) {
			super(usbComm);
			mCmdHead = CmdHead.RFID_RadioSetRegion;
		}

		public boolean setCmd(Region region) {
			mParam.add(region.bRegion);
			composeCmd();
			delay(200);
			
			return checkStatus();
		}

		public boolean setCmd(byte region) {
			mParam.add(region);
			composeCmd();
			delay(200);
			
			return checkStatus();
		}
	}

	
	/************************************************************
	 **					RFID_RadioGetRegion						*
	 ************************************************************/
	static final class RFID_RadioGetRegion extends MtiCmd {
		public RFID_RadioGetRegion(UsbCommunication usbComm) {
			super(usbComm);
			mCmdHead = CmdHead.RFID_RadioGetRegion;
		}

		public boolean setCmd() {
			composeCmd();
			delay(200);
			
			return checkStatus();
		}

		public byte getRegion() {
			return mResponse[3];
		}
	}

}
