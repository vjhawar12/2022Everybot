package frc.robot; 

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.VictorSPXControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.revrobotics.CANSparkMax;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.PS4Controller;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;

public class Robot extends TimedRobot implements Constants {
  private VictorSPX frontRight = new VictorSPX(FRONT_RIGHT);  
  private VictorSPX frontLeft = new VictorSPX(FRONT_LEFT);  
  private VictorSPX rearRight = new VictorSPX(REAR_RIGHT);  
  private VictorSPX rearLeft = new VictorSPX(REAR_LEFT);
  private CANSparkMax arm = new CANSparkMax(ARM, MotorType.kBrushless); 
  private CANSparkMax climber = new CANSparkMax(CLIMBER, MotorType.kBrushless); 
  private VictorSPX intake = new VictorSPX(INTAKE); 
  private DifferentialDrive drive = new DifferentialDrive(frontLeft, frontRight); 
  private PS4Controller controller = new PS4Controller(0); 

  private boolean armUp = true;
  private double lastBurstTime = 0;

  private double autoStart = 0;
  private boolean goForAuto = false;

  private double getTime() {
    return Timer.getFPGATimestamp(); 
  }

  private void autoMove(double velocity) {
    rearLeft.follow(frontLeft); 
    rearRight.follow(frontRight);  
    drive.arcadeDrive(velocity, velocity);
  }

  private void teleopMove(double forward, double horizontal) {
    rearLeft.follow(frontLeft); 
    rearRight.follow(frontRight);  
    drive.arcadeDrive(horizontal, forward);
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
    double forward = controller.getRawAxis(JOYSTICK_LEFT); 
    double horizontal = controller.getRawAxis(JOYSTICK_RIGHT); 
    teleopMove(forward, horizontal);
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
