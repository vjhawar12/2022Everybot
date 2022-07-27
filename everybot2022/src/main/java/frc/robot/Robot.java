package frc.robot; 

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.VictorSPXControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.PS4Controller;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;



public class Robot extends TimedRobot implements Constants {
  private CANSparkMax frontRight = new CANSparkMax(FRONT_RIGHT, MotorType.kBrushed);  
  private CANSparkMax frontLeft = new CANSparkMax(FRONT_LEFT, MotorType.kBrushed);  
  private CANSparkMax rearRight = new CANSparkMax(REAR_RIGHT, MotorType.kBrushed);  
  private CANSparkMax rearLeft = new CANSparkMax(REAR_LEFT, MotorType.kBrushed);
  private CANSparkMax arm = new CANSparkMax(ARM, MotorType.kBrushless); 
  private CANSparkMax climber = new CANSparkMax(CLIMBER, MotorType.kBrushless); 
  private VictorSPX intake = new VictorSPX(INTAKE); 

  private PS4Controller controller = new PS4Controller(0); 

  private boolean armUp = true;
  private double lastBurstTime = 0;

  private double autoStart = 0;
  private boolean goForAuto = false;

  private double getTime() {
    return Timer.getFPGATimestamp(); 
  }

  private void autoMove(double velocity) {
    frontRight.set(velocity); 
    frontLeft.set(velocity); 
    rearRight.set(velocity); 
    rearLeft.set(velocity); 
  }

  private void teleopMove(double right, double left) {
    frontRight.set(right); 
    rearRight.set(right); 
    frontLeft.set(left); 
    rearLeft.set(left); 
  }

  private void setIntake() {
    if (controller.getRawButton(5)) {
      intake.set(VictorSPXControlMode.PercentOutput, 1); 
    } else if (controller.getRawButton(7)) {
      intake.set(VictorSPXControlMode.PercentOutput, -1);
    } else {
      intake.set(VictorSPXControlMode.PercentOutput, 0);
    }
  }

  private void stop() {
    autoMove(0); 
  }

  private void moveArm() {
    if (armUp) {
      if (getTime() - lastBurstTime < armTimeUp) {
        arm.set(armTravel); 
      } else {
        arm.set(armHoldUp); 
      }
    } else {
      if (getTime() - lastBurstTime < armTimeDown) {
        arm.set(-armTravel); 
      } else {
        arm.set(-armHoldUp); 
      }
    }

    double autoTimeElapsed = getTime() - autoStart;
    if (goForAuto) {
      if (autoTimeElapsed < 3) {
        intake.set(ControlMode.PercentOutput, -1); 
      } else if (autoTimeElapsed < 6) {
        intake.set(ControlMode.PercentOutput, 0); 
        autoMove(-0.3);
      } else {
        intake.set(ControlMode.PercentOutput, 0); 
        stop(); 
      }
    } 
  }
  
  private void climb() {
    climber.set(100);
  }

  @Override
  public void robotInit() {
    arm.setIdleMode(IdleMode.kBrake);
  }
  
  @Override
  public void autonomousInit() {
    autoStart = getTime(); 
  }

  @Override
  public void autonomousPeriodic() {
    moveArm(); 
  }

  @Override
  public void teleopPeriodic() {
    double forwardSpeed = -controller.getRawAxis(1); 
    double turnSpeed = -controller.getRawAxis(2); 

    double driveLeft = forwardSpeed - turnSpeed; 
    double driveRight = forwardSpeed + turnSpeed; 

    teleopMove(driveRight, driveLeft); 
    setIntake(); 
    moveArm(); 

    if(controller.getRawButtonPressed(6) && !armUp) {
      lastBurstTime = getTime();  
      armUp = true; 
    } else if (controller.getRawButtonPressed(8) && armUp) {
      lastBurstTime = getTime(); 
      armUp = false; 
    }
  }
}
