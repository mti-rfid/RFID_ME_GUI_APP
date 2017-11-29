package com.mti.rfid.minime;

public class CMD_PwrMgt {


	public enum PowerState {
		Ready((byte)0x00),
		Standby((byte)0x01),
		Sleep((byte)0x02);
		
		private byte bPowerState;
		
		PowerState(byte bPowerState) {
			this.bPowerState = bPowerState;
		}
	}

	
	/************************************************************
	 **					RFID_PowerEnterPowerState				*
	 ************************************************************/
	static final class RFID_PowerEnterPowerState extends MtiCmd {
		public RFID_PowerEnterPowerState(UsbCommunication usbComm) {
			super(usbComm);
			mCmdHead = CmdHead.RFID_PowerEnterPowerState;
		}

		public boolean setCmd(PowerState powerState) {
			mParam.clear();
			mParam.add(powerState.bPowerState);

			composeCmd();
			delay(200);
			
			return checkStatus();
		}
	}

	
	/************************************************************
	 **					RFID_PowerSetIdleTime					*
	 ************************************************************/
	static final class RFID_PowerSetIdleTime extends MtiCmd {
		public RFID_PowerSetIdleTime(UsbCommunication usbComm) {
			super(usbComm);
			mCmdHead = CmdHead.RFID_PowerSetIdleTime;
		}

		public boolean setCmd(PowerState powerState) {
			mParam.clear();
			mParam.add(powerState.bPowerState);

			composeCmd();
			delay(200);
			
			return checkStatus();
		}
	}

	
	/************************************************************
	 **					RFID_PowerGetIdleTime					*
	 ************************************************************/
	static final class RFID_PowerGetIdleTime extends MtiCmd {
		public RFID_PowerGetIdleTime(UsbCommunication usbComm) {
			super(usbComm);
			mCmdHead = CmdHead.RFID_PowerGetIdleTime;
		}

		public boolean setCmd(PowerState powerState) {
			mParam.clear();
			mParam.add(powerState.bPowerState);

			composeCmd();
			delay(200);
			
			return checkStatus();
		}
	}
}
