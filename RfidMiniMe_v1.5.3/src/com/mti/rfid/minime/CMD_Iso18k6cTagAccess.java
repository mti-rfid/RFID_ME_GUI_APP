package com.mti.rfid.minime;

public class CMD_Iso18k6cTagAccess {
	
	public enum LinkFreqSet {
		DontChange((byte)0x00),
		Change((byte)0x01);
		
		private byte bLinkFreqSet;
		
		LinkFreqSet(byte bLinkFreqSet) {
			this.bLinkFreqSet = bLinkFreqSet;
		}
	}

	public enum LinkFreqValue {
		_40kHz((byte)0x00),
		_160kHz((byte)0x06),
		_213kHz((byte)0x08),
		_256kHz((byte)0x09),
		_320kHz((byte)0x0C),
		_640kHz((byte)0x0F);
		
		private byte bLinkFreqValue;
		
		LinkFreqValue(byte bLinkFreqValue) {
			this.bLinkFreqValue = bLinkFreqValue;
		}
	}

	public enum MillerSet {
		DontChange((byte)0x00),
		Change((byte)0x01);
		
		private byte bMillerSet;
		
		MillerSet(byte bMillerSet) {
			this.bMillerSet = bMillerSet;
		}
	}

	public enum MillerValue {
		FM0Baseband((byte)0x00),
		Miller2Subcarrier((byte)0x01),
		Miller4Subcarrier((byte)0x02),
		Miller8Subcarrier((byte)0x03);
		
		private byte bMillerValue;
		
		MillerValue(byte bMillerValue) {
			this.bMillerValue = bMillerValue;
		}
	}

	public enum SessionSet {
		DontChange((byte)0x00),
		Change((byte)0x01);
		
		private byte bSessionSet;
		
		SessionSet(byte bSessionSet) {
			this.bSessionSet = bSessionSet;
		}
	}

	public enum SessionValue {
		S0Session((byte)0x00),
		S1Session((byte)0x01),
		S2Session((byte)0x02),
		S3Session((byte)0x03),
		SL((byte)0x04);
		
		private byte bSessionValue;
		
		SessionValue(byte bSessionValue) {
			this.bSessionValue = bSessionValue;
		}
	}

	public enum TRextSet {
		DontChange((byte)0x00),
		Change((byte)0x01);
		
		private byte bTRextSet;
		
		TRextSet(byte bTRextSet) {
			this.bTRextSet = bTRextSet;
		}
	}

	public enum TRextValue {
		NoPilotTone((byte)0x00),
		UsePilotTone((byte)0x01);
		
		private byte bTRextValue;
		
		TRextValue(byte bTRextValue) {
			this.bTRextValue = bTRextValue;
		}
	}

	public enum QBeginSet {
		DontChange((byte)0x00),
		Change((byte)0x01);
		
		private byte bQBeginSet;
		
		QBeginSet(byte bQBeginSet) {
			this.bQBeginSet = bQBeginSet;
		}
	}

	public enum SensitivitySet {
		DontChange((byte)0x00),
		Change((byte)0x01);
		
		private byte bSensitivitySet;
		
		SensitivitySet(byte bSensitivitySet) {
			this.bSensitivitySet = bSensitivitySet;
		}
	}

	public enum Action {
		StartInventory((byte)0x01),
		NextTag((byte)0x02),
		GetAllTags((byte)0x03);
		
		private byte bAction;
		
		Action(byte bAction) {
			this.bAction = bAction;
		}
	}

	public enum MemoryBank {
		Reserved((byte)0x00),
		EPC((byte)0x01),
		TID((byte)0x02),
		User((byte)0x03);
		
		private byte bMemoryBank;
		
		MemoryBank(byte bMemoryBank) {
			this.bMemoryBank = bMemoryBank;
		}
	}	

	public enum LockAction {
		Accessible((byte)0x00),
		AlwaysAccessable((byte)0x01),
		PasswordAccessible((byte)0x02),
		AlwaysNotAccessible((byte)0x03);
		
		private byte bLockAction;
		
		LockAction(byte bLockAction) {
			this.bLockAction = bLockAction;
		}
	}

	public enum MemorySpace {
		ReservedKillPassword((byte)0x00),
		ReservedAccessPassword((byte)0x01),
		EPC((byte)0x02),
		TID((byte)0x03),
		User((byte)0x04);
		
		private byte bMemorySpace;
		
		MemorySpace(byte bMemorySpace) {
			this.bMemorySpace = bMemorySpace;
		}
	}
	
	public enum NXPCommand {
		EASStatus((byte)0x01),
		ReadProtectStauts((byte)0x02),
		ConfigWord((byte)0x09);
		
		private byte bNXPCommand;
		
		NXPCommand(byte bNXPCommand) {
			this.bNXPCommand = bNXPCommand;
		}
	}
	
	public enum BitStatus {
		Reset((byte)0x00),
		Set((byte)0x01);
		
		private byte bBitStatus;
		
		BitStatus(byte bBitStatus) {
			this.bBitStatus = bBitStatus;
		}
	}

	
	/************************************************************
	 **				RFID_18K6CSetQueryParameter					*
	 ************************************************************/
	static final class RFID_18K6CSetQueryParameter extends MtiCmd {
		public RFID_18K6CSetQueryParameter(UsbCommunication usbComm) {
			super(usbComm);
			mCmdHead = CmdHead.RFID_18K6CSetQueryParameter;
		}

		public boolean setCmd(LinkFreqSet linkFreqSet, LinkFreqValue linkFreqValue,
								MillerSet millerSet, MillerValue millerValue,
								SessionSet sessionSet, SessionValue sessionValue,
								TRextSet trextSet, TRextValue trextValue,
								QBeginSet qBeginSet, byte qBeginValue,
								SensitivitySet sensitivitySet, byte sensivityValue) {
			mParam.add(linkFreqSet.bLinkFreqSet);
			mParam.add(linkFreqValue.bLinkFreqValue);
			mParam.add(millerSet.bMillerSet);
			mParam.add(millerValue.bMillerValue);
			mParam.add(sessionSet.bSessionSet);
			mParam.add(sessionValue.bSessionValue);
			mParam.add(trextSet.bTRextSet);
			mParam.add(trextValue.bTRextValue);
			mParam.add(qBeginSet.bQBeginSet);
			mParam.add(qBeginValue);
			mParam.add(sensitivitySet.bSensitivitySet);
			mParam.add(sensivityValue);

			composeCmd();
			delay(200);

			return checkStatus();
		}
		
		public boolean setCmd(byte linkFreqSet, byte linkFreqValue, byte millerSet, byte millerValue,
								byte sessionSet, byte sessionValue, byte trextSet, byte trextValue,
								byte qBeginSet, byte qBeginValue, byte sensitivitySet, byte sensivityValue) {
			mParam.add(linkFreqSet);
			mParam.add(linkFreqValue);
			mParam.add(millerSet);
			mParam.add(millerValue);
			mParam.add(sessionSet);
			mParam.add(sessionValue);
			mParam.add(trextSet);
			mParam.add(trextValue);
			mParam.add(qBeginSet);
			mParam.add(qBeginValue);
			mParam.add(sensitivitySet);
			mParam.add(sensivityValue);

			composeCmd();
			delay(200);

			return checkStatus();
		}
		
		public boolean setCmd() {
			for(int i = 0; i < 12; i++)
				mParam.add((byte)0x00);
			
			composeCmd();
			delay(200);

			return checkStatus();
		}
		
		public int getSensivity() {
			return mResponse[14];
		}
		
		public int getLinkFrequency() {
			return mResponse[4];
		}
		
		public int getSession() {
			return mResponse[8];
		}
		
		public int getCoding() {
			return mResponse[6];
		}
		
		public int getQBegin() {
			return mResponse[12];
		}
	}
	
	
	/************************************************************
	 **					RFID_18K6CTagInventory					*
	 ************************************************************/
	static final class RFID_18K6CTagInventory extends MtiCmd {
		public RFID_18K6CTagInventory(UsbCommunication usbComm) {
			super(usbComm);
			mCmdHead = CmdHead.RFID_18K6CTagInventory;
		}

		public boolean setCmd(Action action) {
			mParam.clear();
			mParam.add(action.bAction);
			composeCmd();
			if(action.equals(Action.StartInventory))
				delay(100);
			else
				delay(50);
			
			return checkStatus();
		}

		public byte getTagNumber() {
			return mResponse[3];
		}
		
		public String getTagId() {
			int iEpcLength = mResponse[4] - 2;	// #### minus 2 bytes, bcz epc data = pc + epc ####
			byte[] tagId = new byte[iEpcLength > 0 ? iEpcLength : 0];
			
			for(int i = 0; i < iEpcLength; i++) {
				tagId[i] = mResponse[i + 7];
			}
			return strCmd(tagId);
		}
		
		public String getTag() {
			return strCmd(mResponse);
		}
	}
	
	
	/************************************************************
	 *				RFID_18K6CTagInventoryRSSI					*
	 ************************************************************/
	static final class RFID_18K6CTagInventoryRSSI extends MtiCmd {
		public RFID_18K6CTagInventoryRSSI(UsbCommunication usbComm) {
			super(usbComm);
			mCmdHead = CmdHead.RFID_18K6CTagInventoryRSSI;
		}

		public boolean setCmd(Action action) {
			mParam.add(action.bAction);

			composeCmd();
			delay(200);
			
			return checkStatus();
		}
	}
	
	
	/************************************************************
	 **					RFID_18K6CTagSelect						*
	 ************************************************************/
	static final class RFID_18K6CTagSelect extends MtiCmd {
		public RFID_18K6CTagSelect(UsbCommunication usbComm) {
			super(usbComm);
			mCmdHead = CmdHead.RFID_18K6CTagSelect;
		}

		public boolean setCmd(byte[] maskData) {
			mParam.clear();
			mParam.add((byte)maskData.length);
			for(byte data : maskData)
				mParam.add(data);

			composeCmd();
			delay(50);
			
			return checkStatus();
		}
	}
	
	
	/************************************************************
	 *					RFID_18K6CTagRead						*
	 ************************************************************/
	static final class RFID_18K6CTagRead extends MtiCmd {
		public RFID_18K6CTagRead(UsbCommunication usbComm) {
			super(usbComm);
			mCmdHead = CmdHead.RFID_18K6CTagRead;
		}

		public boolean setCmd(MemoryBank memoryBank, byte memoryAddress, long accessPassword, byte tagDataLength) {
			mParam.add(memoryBank.bMemoryBank);
			mParam.add(memoryAddress);
			for(int i = 3; i >= 0; i--)
				mParam.add((byte)(accessPassword >> i * 8));
			mParam.add(tagDataLength);
			
			composeCmd();
			delay(200);
			
			return checkStatus();
		}
	}
	
	
	/************************************************************
	 **					RFID_18K6CTagWrite						*
	 ************************************************************/
	static final class RFID_18K6CTagWrite extends MtiCmd {
		public RFID_18K6CTagWrite(UsbCommunication usbComm) {
			super(usbComm);
			mCmdHead = CmdHead.RFID_18K6CTagWrite;
		}

		public boolean setCmd(MemoryBank memoryBank, byte memoryAddress, long accessPassword, byte[] tagData) {
			mParam.add(memoryBank.bMemoryBank);
			mParam.add(memoryAddress);
			for(int i = 3; i >= 0; i--)
				mParam.add((byte)(accessPassword >> i * 8));
			mParam.add((byte)(tagData.length / 2));
			for(byte data : tagData)
				mParam.add(data);

			composeCmd();
			delay(500);
			
			return checkStatus();
		}
	}
	
	
	/************************************************************
	 *					RFID_18K6CTagKill						*
	 ************************************************************/
	static final class RFID_18K6CTagKill extends MtiCmd {
		public RFID_18K6CTagKill(UsbCommunication usbComm) {
			super(usbComm);
			mCmdHead = CmdHead.RFID_18K6CTagKill;
		}

		public boolean setCmd(long accessPassword) {
			for(int i = 3; i >= 0; i--)
				mParam.add((byte)(accessPassword >> i * 8));

			composeCmd();
			delay(500);
			
			return checkStatus();
		}
	}
	
	
	/************************************************************
	 **					RFID_18K6CTagLock						*
	 ************************************************************/
	static final class RFID_18K6CTagLock extends MtiCmd {
		public RFID_18K6CTagLock(UsbCommunication usbComm) {
			super(usbComm);
			mCmdHead = CmdHead.RFID_18K6CTagLock;
		}

		public boolean setCmd(LockAction lockAction, MemorySpace memorySpace, long accessPassword) {
			mParam.add(lockAction.bLockAction);
			mParam.add(memorySpace.bMemorySpace);
			for(int i = 3; i >= 0; i--)
				mParam.add((byte)(accessPassword >> i * 8));

			composeCmd();
			delay(1000);
			
			return checkStatus();
		}
	}
	
	
	/************************************************************
	 *				RFID_18K6CTagBlockWrite						*
	 ************************************************************/
	static final class RFID_18K6CTagBlockWrite extends MtiCmd {
		public RFID_18K6CTagBlockWrite(UsbCommunication usbComm) {
			super(usbComm);
			mCmdHead = CmdHead.RFID_18K6CTagBlockWrite;
		}

		public boolean setCmd(MemoryBank memoryBank, byte memoryAddress, long accessPassword, byte[] tagData, int delayTime) {
			mParam.add(memoryBank.bMemoryBank);
			mParam.add(memoryAddress);
			for(int i = 3; i >= 0; i--)
				mParam.add((byte)(accessPassword >> i * 8));
			mParam.add((byte)(tagData.length / 2));
			for(byte data : tagData)
				mParam.add(data);

			composeCmd();
			delay(500);
			
			return checkStatus();
		}
	}
	
	
	/************************************************************
	 *					RFID_18K6CTagNXPCommand					*
	 ************************************************************/
	static final class RFID_18K6CTagNXPCommand extends MtiCmd {
		public RFID_18K6CTagNXPCommand(UsbCommunication usbComm) {
			super(usbComm);
			mCmdHead = CmdHead.RFID_18K6CTagNXPCommand;
		}

		public boolean setCmd(NXPCommand nxpCommand, BitStatus bitStatus, long accessPassword, short configWord) {
			mParam.add(nxpCommand.bNXPCommand);
			mParam.add(bitStatus.bBitStatus);
			for(int i = 3; i >= 0; i--)
				mParam.add((byte)(accessPassword >> i * 8));
			for(int i = 1; i >= 0; i--)
				mParam.add((byte)(configWord >> i * 8));

			composeCmd();
			delay(500);
			
			return checkStatus();
		}
	}
	
	
	/************************************************************
	 *				RFID_18K6CTagNXPTriggerEASAlarm				*
	 ************************************************************/
	static final class RFID_18K6CTagNXPTriggerEASAlarm extends MtiCmd {
		public RFID_18K6CTagNXPTriggerEASAlarm(UsbCommunication usbComm) {
			super(usbComm);
			mCmdHead = CmdHead.RFID_18K6CTagNXPTriggerEASAlarm;
		}

		public boolean setCmd() {
			composeCmd();
			delay(500);
			
			return checkStatus();
		}
	}
}
