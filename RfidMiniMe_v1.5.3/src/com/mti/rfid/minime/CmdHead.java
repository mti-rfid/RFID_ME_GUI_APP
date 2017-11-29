package com.mti.rfid.minime;

public enum CmdHead {
	// RFID Module Configuration
	RFID_RadioSetRegion((byte)0xA8),
	RFID_RadioGetRegion((byte)0xAA),
	// Antenna Port Operation
	RFID_AntennaPortSetPowerLevel((byte)0xC0),
	RFID_AntennaPortGetPowerLevel((byte)0xC2),
	RFID_AntennaPortSetFrequency((byte)0x41),
	RFID_AntennaPortSetOperation((byte)0xE4),
	RFID_AntennaPortCtrlPowerState((byte)0x18),
	RFID_AntennaPortTransmitPattern((byte)0xE6),
	RFID_AntennaPortTransmitPulse((byte)0xEA),
	// ISO 18000-6C Tag Access
	RFID_18K6CSetQueryParameter((byte)0x59),
	RFID_18K6CTagInventory((byte)0x31),
	RFID_18K6CTagInventoryRSSI((byte)0x43),
	RFID_18K6CTagSelect((byte)0x33),
	RFID_18K6CTagRead((byte)0x37),
	RFID_18K6CTagWrite((byte)0x35),
	RFID_18K6CTagKill((byte)0x3D),
	RFID_18K6CTagLock((byte)0x3B),
	RFID_18K6CTagBlockWrite((byte)0x70),
	RFID_18K6CTagNXPCommand((byte)0x45),
	RFID_18K6CTagNXPTriggerEASAlarm((byte)0x72),
	// ISO 18000-6B Tag Access
	RFID_18K6BTagInventory((byte)0x3F),
	RFID_18K6BTagRead((byte)0x49),
	RFID_18K6BTagWrite((byte)0x47),
	// RFID Module Firmware Access
	RFID_MacGetModuleID((byte)0x10),
	RFID_MacGetDebugValue((byte)0xA2),
	RFID_MacBypassWriteRegister((byte)0x1A),
	RFID_MacBypassReadRegister((byte)0x1C),
	RFID_MacWriteOemData((byte)0xA4),
	RFID_MacReadOemData((byte)0xA6),
	RFID_MacSoftReset((byte)0xA0),
	RFID_MacEnterUpdateMode((byte)0xD0),
	// RFID Module Manufacturer Engineering
	RFID_EngSetExternalPA((byte)0xE0),
	RFID_EngGetAmbientTemp((byte)0xE2),
	RFID_EngGetForwardRFPower((byte)0xEC),
	RFID_EngTransmitSerialPattern((byte)0xE8),
	RFID_EngWriteFullOemData((byte)0xEE),
	// RFID Module Power Management
	RFID_PowerEnterPowerState((byte)0x02),
	RFID_PowerSetIdleTime((byte)0x04),
	RFID_PowerGetIdleTime((byte)0x06);

	private byte byte1st;
	
	CmdHead(byte byte1st) {
		this.byte1st = byte1st;
	}
	
	public byte get1stCmd() {
		return this.byte1st;
	}
}
