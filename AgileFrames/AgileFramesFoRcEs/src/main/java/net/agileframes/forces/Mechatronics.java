package net.agileframes.forces;
/**
 * <b>Interface of Mechatronics-module.</b>
 * <p>
 * Extend this interface to define the mechatronics module of your machine,
 * it will implement the interface actually defining your mechatronics module.
 * @author  D.G. Lindeijer
 * @version 0.1
 */
public interface  Mechatronics {
  /**
   * Returns the current setting.<p>
   * The current setting will be obtained from memory, not from
   * reading the setting from the mechatronics-device.
   * @return  an array of bytes representing the current setting
   */
  public byte[] getCurrentSetting();
  /**
   * Writes the desired setting.<p>
   * The desired setting will be written to the mechatronics-device immediately.
   * @param   setting   the desired setting
   */
  public void writeSetting(byte[] setting);
}