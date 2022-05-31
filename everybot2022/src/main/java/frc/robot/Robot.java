// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import com.ctre.phoenix.motorcontrol.can.VictorSPX; 
import edu.wpi.first.wpilibj.Joystick;

public class Robot extends TimedRobot {

  VictorSPX frontLeft = new VictorSPX(deviceNumber);
  VictorSPX frontRight = new VictorSPX(deviceNumber); 
  VictorSPX rearLeft = new VictorSPX(deviceNumber); 
  VictorSPX rearRight = new VictorSPX(deviceNumber); 

  Joystick joystick = new Joystick(port); 

  @Override
  public void robotInit() {
   
  }

  @Override
  public void robotPeriodic() {

  }

  
  @Override
  public void autonomousInit() {
  
  }

  @Override
  public void autonomousPeriodic() {
    
  }

  @Override
  public void teleopInit() {}

  @Override
  public void teleopPeriodic() {}
}
