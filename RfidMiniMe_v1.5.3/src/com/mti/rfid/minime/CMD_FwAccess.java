package com.mti.rfid.minime;

public class CMD_FwAccess {
	
	public enum ModuleId {
		FirmwareId((byte)0x00),
		HardwareId((byte)0x01),
		OEMCfgId((byte)0x02),
		OEMCfgUpdateId((byte)0x03);
		
		private byte bModuleId;
		
		ModuleId(byte bModuleId) {
			this.bModuleId = bModuleId;
		}
	}
	
	
	/************************************************************
	 *					RFID_MacGetModuleID						*
	 ************************************************************/
	static final class RFID_MacGetModuleID extends MtiCmd {
		public RFID_MacGetModuleID(UsbCommunication usbComm) {
			super(usbComm);
			mCmdHead = CmdHead.RFID_MacGetModuleID;
		}

		public boolean setCmd(ModuleId moduleId) {
			mParam.add(moduleId.bModuleId);

			super.composeCmd();
			return true;
		}
	}
	
	
	/************************************************************
	 *					RFID_MacGetDebugValue					*
	 ************************************************************/
	static final class RFID_MacGetDebugValue extends MtiCmd {
		public RFID_MacGetDebugValue(UsbCommunication usbComm) {
			super(usbComm);
			mCmdHead = CmdHead.RFID_MacGetDebugValue;
		}

		public boolean setCmd() {
			super.composeCmd();
			return true;
		}
	}
	
	
	/************************************************************
	 *				RFID_MacBypassWriteRegister					*
	 ************************************************************/
	static final class RFID_MacBypassWriteRegister extends MtiCmd {
		public RFID_MacBypassWriteRegister(UsbCommunication usbComm) {
			super(usbComm);
			mCmdHead = CmdHead.RFID_MacBypassWriteRegister;
		}

		public boolean setCmd(byte regAddress, byte[] regData) {
			mParam.add(regAddress);
			for(byte register : regData)
				mParam.add(register);

			super.composeCmd();
			return true;
		}
	}
	
	
	/************************************************************
	 *				RFID_MacBypassReadRegister					*
	 ************************************************************/
	static final class RFID_MacBypassReadRegister extends MtiCmd {
		public RFID_MacBypassReadRegister(UsbCommunication usbComm) {
			super(usbComm);
			mCmdHead = CmdHead.RFID_MacBypassReadRegister;
		}

		public boolean setCmd(byte regAddress) {
			mParam.add(regAddress);

			super.composeCmd();
			return true;
		}
	}
	
	
	/************************************************************
	 *					RFID_MacWriteOemData					*
	 ************************************************************/
	static final class RFID_MacWriteOemData extends MtiCmd {
		public RFID_MacWriteOemData(UsbCommunication usbComm) {
			super(usbComm);
			mCmdHead = CmdHead.RFID_MacWriteOemData;
		}

		public boolean setCmd(int oemCfgAddress, byte oemCfgData) {
			if(oemCfgAddress < 0x0080 || oemCfgAddress > 0x07ff) {
				// error
				return false;
			} else {
				for(int i = 0; i < 2; i++)
					mParam.add((byte)(oemCfgAddress >> i * 8));
				mParam.add(oemCfgData);
	
				super.composeCmd();
			}
			return true;
		}
	}
	
	
	/************************************************************
	 *					RFID_MacReadOemData						*
	 ************************************************************/
	static final class RFID_MacReadOemData extends MtiCmd {
		public RFID_MacReadOemData(UsbCommunication usbComm) {
			super(usbComm);
			mCmdHead = CmdHead.RFID_MacReadOemData;
		}

		public boolean setCmd(int oemCfgAddress) {
			if(oemCfgAddress < 0x0000 || oemCfgAddress > 0x1fff) {
				// error
				return false;
			} else {
				for(int i = 1; i >= 0; i--)
					mParam.add((byte)(oemCfgAddress >> i * 8));
	
				super.composeCmd();
			}
			delay(50);
			return checkStatus();
		}
		
		public byte getData() {
			return mResponse[3];
		}
	}
	
	
	/************************************************************
	 *					RFID_MacSoftReset						*
	 ************************************************************/
	static final class RFID_MacSoftReset extends MtiCmd {
		public RFID_MacSoftReset(UsbCommunication usbComm) {
			super(usbComm);
			mCmdHead = CmdHead.RFID_MacSoftReset;
		}

		public boolean setCmd() {
			super.composeCmd();
			return true;
		}
	}
	
	
	/************************************************************
	 *					RFID_MacEnterUpdateMode					*
	 ************************************************************/
	static final class RFID_MacEnterUpdateMode extends MtiCmd {
		public RFID_MacEnterUpdateMode(UsbCommunication usbComm) {
			super(usbComm);
			mCmdHead = CmdHead.RFID_MacEnterUpdateMode;
		}

		public boolean setCmd() {
			super.composeCmd();
			return true;
		}
	}

}
