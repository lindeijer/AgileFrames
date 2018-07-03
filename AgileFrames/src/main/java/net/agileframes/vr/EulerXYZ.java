package net.agileframes.vr;

import javax.vecmath.*;

/**
 * <b>EulerXYZ is a mathematical class used in calculating position and orientation of bodies.</b><p>
 * Given an arbitrary rotation about the three axis, ie X, Y, Z it will calculate a so called
 * Euler matrix, which is then passed to the J3D structure.
 * When given an eulermatrix is will deduce the rotation about the three axis.
 * @author  F.A. van Dijk
 * @version 0.1
 */
 public class EulerXYZ {
  private Matrix4d rx = null;
  private Matrix4d ry = null;
  private Matrix4d rz = null;
  private Matrix4d rotation = null;

 /**
  * Constructs an Euler matrix from a rotation matrix<p>
  * @param  rotation  the matrix to construct from
  */
  public EulerXYZ(Matrix4d rotation) {
    this();
    this.rotation.set(rotation);
  }

  /**
   * Constructs an empty Eulermatrix.<p>
   */
  public EulerXYZ(){
    this.rx = new Matrix4d();
    this.ry = new Matrix4d();
    this.rz = new Matrix4d();
    this.rotation = new Matrix4d();
  }


  /**
   * Constructs a new EulerXYZ object that represent a rotation about the X, Y and Z axis respectively.<p>
   * @param x the angle to rotate about the x-axis
   * @param y the angle to rotate about the y-axis
   * @param z the angle to rotate about the z-axis
   */
  public EulerXYZ(double x, double y, double z) {
    this();
    this.set(x, y, z);
  }

  /**
   * Sets new angle rotation values in an EulerXYZ matrix.<p>
   * @param x the angle to rotate about the x-axis
   * @param y the angle to rotate about the y-axis
   * @param z the angle to rotate about the z-axis
   */
  public Matrix4d set(double x, double y, double z) {
    this.rx.rotX(x);
    this.ry.rotY(y);
    this.rz.rotZ(z);
    calcXYZ();
    return this.rotation;
  }

  /**
   * Sets new angle rotation values in an EulerXYZ matrix.<p>
   * @param m the new rotation matrix
   */
  public void set(Matrix4d m){
    this.rotation.set(m);
  }

  /**
   * Calculates the EulerXYZ matrix.<p>
   */
  private void calcXYZ() {
    this.rotation.set(rz);
    this.rotation.mul(ry);
    this.rotation.mul(rx);
  }

  /**
   * Returns the rotation matrix.<p>
   * @return the rotation matrix
   */
  public Matrix4d getRotationMatrix(){
    Matrix4d x = new Matrix4d(this.rotation);
    return x;
  }
  /**
   * Sets the rotation on the rotation matrix.<p>
   * @param       rotationMatrix the matrix to set
   */
  public void get(Matrix4d rotationMatrix){// TODO: change the name of this method!
    rotationMatrix.set(this.rotation);
  }
  /**
   * Sets the three euler angles from an Eulermatrix.<p>
   * @param angles a Vector3d containing the three angles
   */
  public void get(Vector3d angles){//TODO: change the name of this method!
    angles = this.calculateEulerAngles();
  }

  /**
   * Validates a certain triplet.<p>
   * Given a combination of three euler-angles, it checks whether they meet other calculations
   * @param   x the angle to rotate about the x axis
   * @param   y the angle to rotate about the y axis
   * @param   z the angle to rotate about the z axis
   * @return  <code><b>true</b></code> iff they meet other calculations<br>
   *          <code><b>false</b></code> otherwise
   */
  private boolean validate(double x, double y, double z) {
    boolean match = false;
    if(equal(this.rotation.m00, Math.cos(y)*Math.cos(z)))
      if (equal(this.rotation.m01, Math.sin(x)*Math.sin(y)*Math.cos(z)-Math.cos(x)*Math.sin(z)))
        if (equal(this.rotation.m02, Math.cos(x)*Math.sin(y)*Math.cos(z)+Math.sin(x)*Math.sin(z)))
          if (equal(this.rotation.m11, Math.sin(x)*Math.sin(y)*Math.sin(z)+Math.cos(x)*Math.cos(z)))
            if (equal(this.rotation.m12, Math.cos(x)*Math.sin(y)*Math.sin(z)-Math.sin(x)*Math.cos(z)))
              if (equal(this.rotation.m22, Math.cos(x)*Math.cos(y)))
                match = true;
    return match;
  }

  /**
   * Calculates the absolute difference between to doubles a verifies if this difference meets a certain precision<p>
   * @param a the first double
   * @param b the second double
   * @return whether or not the difference is smaller than a certain value
   */
  private boolean equal(double a, double b){
    double difference=Math.abs(a-b);
    double epsilon = 0.001;
    if (difference < epsilon)
      return true;
    else
      return false;
  }


  /**
   * Calculates the three euler angles from an euler matrix.<p>
   * @return the three euler angles
   */
  public Vector3d calculateEulerAngles() {
    int index=0;
    double y1;
    double y2;

    Vector3d ea = new Vector3d();

    double x11;
    double x12;
    double x21;
    double x22;

    double z11;
    double z12;
    double z21;
    double z22;

    boolean trouble = (equal(this.rotation.m20, -1) || equal(this.rotation.m20, 1));

    if (!trouble) {
      y1 = -Math.asin(this.rotation.m20);
      y2 = Math.PI-y1;

      x11 = Math.asin(this.rotation.m21/Math.cos(y1));
      x12 = Math.PI-x11;
      x21 = Math.asin(this.rotation.m21/Math.cos(y2));
      x22 = Math.PI-x21;

      z11 = Math.asin(this.rotation.m10/Math.cos(y1));
      z12 = Math.PI-z11;
      z21 = Math.asin(this.rotation.m10/Math.cos(y2));
      z22 = Math.PI-z21;

      double triplet[][] = {
                           {x11, y1, z11},
                           {x12, y1, z11},
                           {x11, y1, z12},
                           {x12, y1, z12},
                           {x21, y2, z21},
                           {x22, y2, z21},
                           {x21, y2, z22},
                           {x22, y2, z22},
                         };

      boolean matched=false;
      for (index = 0;index<8 && !matched ;index++) {
        matched=validate(triplet[index][0], triplet[index][1], triplet[index][2]);
      }
      if (matched) {
        ea.x = triplet[index-1][0];
        ea.y = triplet[index-1][1];
        ea.z = triplet[index-1][2];
      } // a match was found !!
      else {
        System.err.println("A Humongous error has just occured !!!");
        ea.x = Double.NaN;
        ea.y = Double.NaN;
        ea.z = Double.NaN;
      }
    } // !trouble
    else { // trouble, because divide by zero
      double diff1, diff2;
      double x, z;
      double sollution1;
      double sollution2;

      // Is Y rotation equal to PI/2 ??
      if (equal(this.rotation.m20, -1)){
        sollution1 = Math.asin(this.rotation.m01);
        sollution2 = Math.PI - sollution1;
        if (equal(this.rotation.m02, Math.cos(sollution1))) {
          ea.x = sollution1;
          ea.y = Math.PI/2;
          ea.z = 0;
        } // if m13==1
        else {
          ea.x = sollution2;
          ea.y = Math.PI/2;
          ea.z = 0;
        } // m31==-1
      } //Y rotation = 1
      // Y rotation is equal to -PI/2
      else {
        sollution1 = -Math.asin(this.rotation.m01);
        sollution2 = Math.PI - sollution1;
        if (equal(this.rotation.m02, -Math.cos(sollution1))) {
          ea.x = sollution1;
          ea.y = -Math.PI/2;
          ea.z = 0;
        } // if m13==1
        else {
          ea.x = sollution2;
          ea.y = -Math.PI/2;
          ea.z = 0;
        } // m13==-1
      } // Y rotation equal to -1
    } // trouble, divide by zero
    return ea;
  }//calculateEulerAngles

}