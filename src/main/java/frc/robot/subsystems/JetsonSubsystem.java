/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class JetsonSubsystem extends SubsystemBase {
  /**
   * Creates a new JetsonSubsystem.
   */
  private NetworkTable table;
  private NetworkTableEntry colour;
  private String colors;
  

  public JetsonSubsystem() {
    table = NetworkTableInstance.getDefault().getTable("SmartDashboard");
    colour = table.getEntry("color");
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    colors = colour.getString("null");
  }
  public String getColor() {
    return colors;
  }

}
