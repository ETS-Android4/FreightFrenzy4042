package org.firstinspires.ftc.teamcode.robot_components.robot;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.Optional;

@RequiresApi(api = Build.VERSION_CODES.N) // needed to use Optional (not actually, but android studio complains)
public class DuckSpinner {
    CRServo motor = null;
    double muGSquared;
    double radius;
    double w4r2; //This will be the angular velocity of the spinner ^4 times spinner radius^2
    private ElapsedTime runtime = null;
    private Optional<Telemetry> telemetry; // used to log power if not null
                                           // Optional<e> prevents NullReferenceExceptions
    private boolean isRunning = false;
//    private boolean useEncoder = false;
    //inputs: radius from center of wheel to duck and current velocity
    //constants: gravity, coefficient of friction, moment of inertia
    //output power
    final double G_CONST = -9.8; // m/s^2
    final double SPINNER_MOMENT_OF_INERTIA = 1000; // probably not accurate, needs tweaking
    final double MAX_MOTOR_TORQUE = 18.7;
    final double SPIN_TIME_BEFORE_STOP = 1000;//3.3; // this needs tweaking
    long previousTicks = 0;
    double previousElapsedTime = 0;
    double SPINNER_TABLE_RATIO = 8; //Ratio between the spinner radius and table radius
    private ElapsedTime VElapsedTime;


    // TODO: SET DIRECTION !!!!

    public DuckSpinner(CRServo motor, double frictionCoefficient, double radius) {
        this(motor, frictionCoefficient, radius, null);
    }

    public DuckSpinner(
            CRServo motor,
            double frictionCoefficient,
            double radius,
            Telemetry telemetry //,
//            boolean useEncoders
    ){
        this.motor = motor;
        this.muGSquared = Math.pow((frictionCoefficient * G_CONST), 2);
        this.radius = radius;
        this.runtime = new ElapsedTime();
        //this.telemetry = Optional.ofNullable(telemetry);

//        this.useEncoder = useEncoders;
    }
    // w is the rotational velocity of the motor.
    //Assuming no slippage, the duck is traveling at W * DuckRadius
    private double calcAccel(double w) {
        w4r2 = Math.pow(w, 4) * Math.pow(radius, 2);
        if(muGSquared > w4r2) {
            return (Math.sqrt(muGSquared - w4r2)) / radius;
        } else {
            return 1000; //returns a high value for speed to stop the wheel from spinning
        }
    }

    private double calcPower(double alpha) {
        double torque =  alpha / SPINNER_MOMENT_OF_INERTIA;
        return torque / MAX_MOTOR_TORQUE;
    }

    public void startRunning() {
        runtime.reset();
        isRunning = true;
    }

    public void stopRunning() {
        motor.setPower(0);
        isRunning = false;
    }
    /*public double getVelocity() {
        long deltaTicks = (motor.getCurrentPosition() - previousTicks);
        double deltaTime = VElapsedTime.seconds() - previousElapsedTime;
        previousTicks = motor.getCurrentPosition();
        previousElapsedTime = VElapsedTime.seconds();
        return (deltaTicks / deltaTime);
    } */

    public ElapsedTime getRuntime() {
        return runtime;
    }

    public boolean isPowered() {
        return isRunning;
    }


    /**
     * call this repeatedly to run the spinner
     * @return true if done spinning, else false
     */

    public boolean update() {
        double time = runtime.seconds();
        /*
        telemetry.ifPresent(telemetry1 -> { // if telemetry is null nothing will happen
            telemetry1.addData("duck time", time);
            telemetry1.addData("duck motor power", motor.getPower());
            if (isRunning) {
                telemetry1.addData("Goal motor power", 0.5);
            } else {
                telemetry1.addData("Goal motor power", 0);
            }
        });
        */
        if (isRunning) {

            //motor.setPower(calcPower(calcAccel(getVelocity() * SPINNER_TABLE_RATIO)));
            return false;
        } else {
            // setting power to zero will make the motor brake and stop
            // TODO: use encoder to make sure the wheel doesn't slip to slow down as fast as possible
            this.stopRunning();
            return true;
        }
    }
}
