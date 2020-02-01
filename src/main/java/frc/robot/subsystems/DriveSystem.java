/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.kauailabs.navx.frc.AHRS;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.drive.Vector2d;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpiutil.math.MathUtil;
import frc.robot.Constants;

public class DriveSystem extends SubsystemBase {
  private CANSparkMax motorRight1;
  private CANSparkMax motorRight2;
  private CANSparkMax motorLeft1; 
  private CANSparkMax motorLeft2;
  private CANPIDController pid;

  public double kP, kI, kD, kIz, kFF, kMaxOutput, kMinOutput, maxRPM, maxVel, minVel, maxAcc, allowedErr;

  private boolean isFieldOriented = false;
  private boolean isPID = false;
  private boolean isSlowMode = false;
  private boolean isTurbo = false;
  
  private static final double ramp_rate = 0.2;
  private static final double voltage_comp = 12.0;
  private static final int current_limit = 50;

  private AHRS NavX;
  private MecanumDrive mecanumDrive;
  /**
   * Creates a new DriveSystem.
   */
  public DriveSystem(CANSparkMax motor4, CANSparkMax motor2, CANSparkMax motor3, CANSparkMax motor1) {
    motorRight1 = motor3;
    motorRight2 = motor1;
    motorLeft1 = motor4;
    motorLeft2 = motor2;

    motorLeft1.setInverted(false);
    motorLeft2.setInverted(false);
    motorRight1.setInverted(false);
    motorRight2.setInverted(false);

    motorLeft1.setSmartCurrentLimit(current_limit);
    motorLeft2.setSmartCurrentLimit(current_limit);
    motorRight1.setSmartCurrentLimit(current_limit);
    motorRight2.setSmartCurrentLimit(current_limit);

    motorLeft1.enableVoltageCompensation(voltage_comp);
    motorLeft2.enableVoltageCompensation(voltage_comp);
    motorRight1.enableVoltageCompensation(voltage_comp);
    motorRight2.enableVoltageCompensation(voltage_comp);

    motorLeft1.setOpenLoopRampRate(ramp_rate);
    motorLeft2.setOpenLoopRampRate(ramp_rate);
    motorRight1.setOpenLoopRampRate(ramp_rate);
    motorRight2.setOpenLoopRampRate(ramp_rate);

    kP = 5e-5; 
    kI = 1e-6;
    kD = 0; 
    kIz = 0; 
    kFF = 0.000156; 
    kMaxOutput = 1; 
    kMinOutput = -1;
    maxRPM = 5700;

    mecanumDrive = new MecanumDrive(motorLeft1, motorLeft2, motorRight1, motorRight2);

    NavX = new AHRS();
  }

  public void Drive(double xSpeed, double ySpeed, double zRotation) {
    if(isFieldOriented == true){
      if(isSlowMode == true)
        mecanumDrive.driveCartesian((xSpeed/2), (ySpeed)/2, (zRotation)/4, -NavX.getAngle());
      else if(isTurbo == true)
        mecanumDrive.driveCartesian(xSpeed, ySpeed, zRotation, -NavX.getAngle());
      else
        mecanumDrive.driveCartesian(xSpeed, ySpeed, (zRotation)/2, -NavX.getAngle());
    }
    else if(isPID == true){
      MathUtil.clamp(xSpeed, -1.0, 1.0);
      //xSpeed = applyDeadband(xSpeed, 0);

      MathUtil.clamp(ySpeed, -1.0, 1.0);
      //ySpeed = applyDeadband(ySpeed, 0);

      Vector2d input = new Vector2d(xSpeed, ySpeed);
      input.rotate(-NavX.getAngle());

      double[] speeds = new double[4];
      speeds[0] = input.x + input.y + zRotation;
      speeds[1] = -input.x + input.y - zRotation;
      speeds[2] = -input.x + input.y + zRotation;
      speeds[3] = input.x + input.y - zRotation;

      //normalize(speeds);

      motorLeft1.set(speeds[0]);
      motorRight1.set(speeds[1]*-1.0);
      motorLeft2.set(speeds[2]);
      motorRight2.set(speeds[3]*-1.0);

      //setPID(motorLeft1);
      //setPID(motorLeft2);
      //setPID(motorRight1);
      //setPID(motorRight2);
    }   
    else
      if(isSlowMode == true)
       mecanumDrive.driveCartesian((xSpeed/2), (ySpeed)/2, (zRotation)/4);
      else if(isTurbo == true)
       mecanumDrive.driveCartesian(xSpeed, ySpeed, zRotation);
      else
        mecanumDrive.driveCartesian(xSpeed*0.8, ySpeed*0.8, (zRotation)/2);
  }

  
  public void zeroGyro(){
    NavX.zeroYaw();
  }

  public void setPID(CANSparkMax motor) {
    pid = motor.getPIDController();
    
    pid.setP(kP);
    pid.setI(kI);
    pid.setD(kD);
    pid.setIZone(kIz);
    pid.setFF(kFF);
    pid.setOutputRange(kMinOutput, kMaxOutput);

    //pid.setReference(500.0, ControlType.kVelocity);

    motor.setSmartCurrentLimit(current_limit);
    motor.enableVoltageCompensation(voltage_comp);
  }
  

  public void PercentOut(double yAxis){
    motorLeft1.set(yAxis);
    motorLeft2.set(yAxis);
    motorRight1.set(yAxis);
    motorRight2.set(yAxis);
  }
  public void setPIDLooped(boolean bool){
    isPID = bool;
  }
  public boolean getPIDLooped(){
    return isPID;
  }
  public void setFieldOriented(boolean bool){
    isFieldOriented = bool;
  }
  public boolean getFieldOriented(){
    return isFieldOriented;
  }
  public void setSlow(boolean slow){
    isSlowMode = slow;
  }
  public boolean getSlow(){
    return isSlowMode;
  }
  public void setTurbo(boolean turbo){
    isTurbo = turbo;
  }
  public boolean getTurbo(){
    return isTurbo;
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
