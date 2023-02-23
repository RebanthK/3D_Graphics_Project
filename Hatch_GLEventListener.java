/* While this file has been adapted from chapter 7 of the labs I declare that this code is my own work */
/* Author Rebanth Kanner rkanner1@sheffield.ac.uk*/

import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
  
public class Hatch_GLEventListener implements GLEventListener {
  
  private static final boolean DISPLAY_SHADERS = false;
    
  public Hatch_GLEventListener(Camera camera) {
    this.camera = camera;
    this.camera.setPosition(new Vec3(4f,12f,18f));
  }
  
  // ***************************************************
  /*
   * METHODS DEFINED BY GLEventListener
   */

  /* Initialisation */
  public void init(GLAutoDrawable drawable) {   
    GL3 gl = drawable.getGL().getGL3();
    System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
    gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); 
    gl.glClearDepth(1.0f);
    gl.glEnable(GL.GL_DEPTH_TEST);
    gl.glDepthFunc(GL.GL_LESS);
    gl.glCullFace(GL.GL_BACK);
    initialise(gl);
    startTime = getSeconds();
  }
  
  /* Called to indicate the drawing surface has been moved and/or resized  */
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    GL3 gl = drawable.getGL().getGL3();
    gl.glViewport(x, y, width, height);
    float aspect = (float)width/(float)height;
    camera.setPerspectiveMatrix(Mat4Transform.perspective(45, aspect));
  }

  /* Draw */
  public void display(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    render(gl);
  }

  /* Clean up memory, if necessary */
  public void dispose(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    light.dispose(gl);
    floor.dispose(gl);
    sphere.dispose(gl);
    cube.dispose(gl);
    sphere_eyes.dispose(gl);
    windowframe1.dispose(gl);
    windowframe2.dispose(gl);
    windowframe3.dispose(gl);
    windowframe4.dispose(gl);
    leftWall.dispose(gl);
    rightWall.dispose(gl);
    sceneWall.dispose(gl);
  }
  
  
  // ***************************************************
  /* INTERACTION AND ANIMATION
   *
   *
   */
   
  private boolean animation = false;
  private String pose = "default";
  private double savedTime = 0;

  private int currenthead2 = 40;
  private int currentlower2 = 40;
  private int currentlower2y = 0;
  private int currentupper2 = -80;

  private int currenthead1 = -20;
  private int currentlower1 = -25;
  private int currentlower1y = 0;
  private int currentupper1 = 50;

  private int targethead1 = currenthead1;
  private int targetlower1 = currentlower1;
  private int targetlower1y = currentlower1y;
  private int targetupper1 = currentupper1;

  private int targethead2 = currenthead2;
  private int targetlower2 = currentlower2;
  private int targetlower2y = currentlower2y;
  private int targetupper2 = currentupper2;

  private boolean lightSwitch = true;

  public void lightSwitch(){
    if (lightSwitch){
      lightSwitch = false;
    }

    else{
      lightSwitch = true;
    }
  }

  public void startlamp1() {
    targetlower1y = 0;
    targethead1 = -20;
    targetlower1 = -25;
    targetupper1 = 50;
  }

  public void startlamp2() {
    targetlower2y = 0;
    targethead2 = 40;
    targetlower2 = 40;
    targetupper2 = -80;
  }
   
  public void lamp1pose1() {
    targetlower1y = 0;
    targethead1 = 20;
    targetlower1 = -40;
    targetupper1 = 70;
  }

  public void lamp2pose1() {
    targetlower2y = 0;
    targetlower2 = 50;
    targetupper2 = -130;
    targethead2 = 50;
  }

  public void lamp1pose2() {
    targetlower1y = -70;
    targethead1 = -40;
    targetlower1 = -30;
    targetupper1 = 30;
  }
   
  public void lamp2pose2() {
    targetlower2y = 70;
    targethead2 = 70;
    targetlower2 = -20;
    targetupper2 = -50;
  }
  
  // ***************************************************
  /* THE SCENE
   * Now define all the methods to handle the scene.
   * This will be added to in later examples.
   */

  private Camera camera;
  private Mat4 perspective;
  private Model floor, sphere, sphere_eyes, cube, windowframe1, windowframe2, windowframe3, windowframe4, windowWall, leftWall, rightWall, sceneWall;
  private Light lightLamp1, lightLamp2, light, light2;
  private SGNode tableRoot, lamp1Root, lamp2Root;

  // Variable declarations for Scene Graphs
  
  private float xPosition = 0;
  private float rotateLowerAngle1Start = -25, rotateLowerAngle1 = rotateLowerAngle1Start;
  private float rotateUpperAngle1Start = 50, rotateUpperAngle1 = rotateUpperAngle1Start;
  private float rotateHead1AngleStart = -20, rotateHead1Angle = rotateHead1AngleStart;
  private float rotateLowerAngle1yStart = 0, rotateLowerAngle1y = rotateLowerAngle1yStart;

  private float rotateLowerAngle2yStart = 0, rotateLowerAngle2y = rotateLowerAngle2yStart;
  private float rotateLowerAngle2Start = 40, rotateLowerAngle2 = rotateLowerAngle2Start;
  private float rotateUpperAngle2Start = -80, rotateUpperAngle2 = rotateUpperAngle2Start;
  private float rotateHead2AngleStart = 40, rotateHead2Angle = rotateHead2AngleStart;
  
  private float spinAngleStart = 50, spinAngle = spinAngleStart;
  private float jumpHeightStart = 0, jumpHeight = jumpHeightStart;
  
  private TransformNode translateX, tableMoveTranslate, leftArmRotate, rightArmRotate, lamp1MoveTranslate, lamp2MoveTranslate, rotateLower1, rotateLower2, rotateLower1y, rotateLower2y,rotateUpper1, rotateUpper2, rotateHead1, rotateHead2, spinEgg, jumpEgg;
  
  private void initialise(GL3 gl) {
    int[] textureId0 = TextureLibrary.loadTexture(gl, "textures/floor.jpg");
    int[] textureId1 = TextureLibrary.loadTexture(gl, "textures/jade.jpg");
    int[] textureId2 = TextureLibrary.loadTexture(gl, "textures/jade_specular.jpg");
    int[] textureId3 = TextureLibrary.loadTexture(gl, "textures/container2.jpg");
    int[] textureId4 = TextureLibrary.loadTexture(gl, "textures/container2_specular.jpg");
    int[] textureId5 = TextureLibrary.loadTexture(gl, "textures/walls.jpg");
    int[] textureId6 = TextureLibrary.loadTexture(gl, "textures/wattBook_specular.jpg");
    int[] textureId7 = TextureLibrary.loadTexture(gl, "textures/background.jpg");
    int[] textureId8 = TextureLibrary.loadTexture(gl, "textures/eyes.jpg");
    
        
    light = new Light(gl);
    light2 = new Light(gl);
    lightLamp1 = new Light(gl);
    lightLamp2 = new Light(gl);
    light.setCamera(camera);
    light2.setCamera(camera);
    lightLamp1.setCamera(camera);
    lightLamp2.setCamera(camera);
    

    // Model declarations
    float size = 16f;

    Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    Shader shader = new Shader(gl, "vs_tt_05.txt", "fs_tt_05.txt");
    Material material = new Material(new Vec3(1.0f, 1f, 1f), new Vec3(1.0f, 1.0f, 1.0f), new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
    Mat4 modelMatrix = Mat4Transform.scale(16,1f,16);
    floor = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId0);

    mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    shader = new Shader(gl, "vs_tt_05.txt", "fs_cube_04.txt");
    material = new Material(new Vec3(1.0f, 1.0f, 1.0f), new Vec3(1f, 1f, 1f), new Vec3(0.3f,0.3f, 0.3f), 32.0f);
    modelMatrix = Mat4Transform.scale(1f,1f,size);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(-size/2 + 0.5f, size/2,-size/2), modelMatrix);
    windowframe1 = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId3, textureId4);

    mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    shader = new Shader(gl, "vs_tt_05.txt", "fs_cube_04.txt");
    material = new Material(new Vec3(0.0f, 0.0f, 0.0f), new Vec3(1f, 1f, 1f), new Vec3(0.3f,0.3f, 0.3f), 32.0f);
    modelMatrix = Mat4Transform.scale(1f,1f,size);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(size/2 -0.5f, size/2,-size/2), modelMatrix);
    windowframe2 = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId3, textureId4);

    mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    shader = new Shader(gl, "vs_tt_05.txt", "fs_cube_04.txt");
    material = new Material(new Vec3(0.0f, 0.0f, 0.0f), new Vec3(1f, 1f, 1f), new Vec3(0.3f,0.3f, 0.3f), 32.0f);
    modelMatrix = Mat4Transform.scale(size,1f,1f);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(0,size -0.5f, -size/2), modelMatrix);
    windowframe3 = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId3, textureId4);

    mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    shader = new Shader(gl, "vs_tt_05.txt", "fs_cube_04.txt");
    material = new Material(new Vec3(0.0f, 0.0f, 0.0f), new Vec3(1f, 1f, 1f), new Vec3(0.3f,0.3f, 0.3f), 32.0f);
    modelMatrix = Mat4Transform.scale(size,1f,1f);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(0,0.5f,-size/2), modelMatrix);
    windowframe4 = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId3, textureId4);

    mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    shader = new Shader(gl, "vs_tt_06.txt", "fs_tt_06.txt");
    material = new Material(new Vec3(0.0f, 0.0f, 0.0f), new Vec3(1f, 1f, 1f), new Vec3(0.3f,0.3f, 0.3f), 32.0f);
    modelMatrix = Mat4Transform.scale(size * 2,2f,size * 2);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(0,2*size/3,-size), modelMatrix);
    sceneWall = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId7, null);

    mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    shader = new Shader(gl, "vs_tt_05.txt", "fs_tt_05.txt");
    material = new Material(new Vec3(1.0f, 1f, 1f), new Vec3(1.0f, 1.0f, 1.0f), new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
    modelMatrix = Mat4Transform.scale(size,1f,size);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundY(90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundZ(-90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(-size/2,size/2,0), modelMatrix);
    leftWall = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId5);
    
    mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    shader = new Shader(gl, "vs_tt_05.txt", "fs_tt_05.txt");
    material = new Material(new Vec3(1f, 1f, 1f), new Vec3(1.0f, 1.0f, 1.0f), new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
    modelMatrix = Mat4Transform.scale(size,1f,size);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundY(90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundZ(90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(size/2,size/2,0), modelMatrix);
    rightWall = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId5);

    mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    shader = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
    material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
    sphere = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId1, textureId2);

    mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    shader = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
    material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
    sphere_eyes = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId8, textureId2);
    
    mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    shader = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
    material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
    cube = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId3, textureId4);


    //Scene Graph for table

    float legHeight = 2f;
    float legWidth = 0.5f;
    float legDepth = 0.5f;
    float tableHeight = 0.5f;
    float tableWidth = 4f;
    float tableDepth = 4f;

    tableRoot = new NameNode("tableRoot");
    tableMoveTranslate = new TransformNode("table transform",Mat4Transform.translate(xPosition,0,0));
    TransformNode tableTranslate = new TransformNode("table transform",Mat4Transform.translate(0, legHeight,0));

    NameNode desktop = new NameNode("desktop");
      Mat4 m = Mat4Transform.scale(tableWidth, tableHeight, tableDepth);
      m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
      TransformNode desktopTransform = new TransformNode("desktop transform", m);
      ModelNode desktopShape = new ModelNode("Cube(desktop)", cube);

    NameNode backRightLeg = new NameNode("backRightleg");
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.translate((tableWidth*0.5f)-(legWidth*0.5f),0,(tableDepth*-0.5f)+(legWidth*0.5f)));
      m = Mat4.multiply(m, Mat4Transform.rotateAroundX(180));
      m = Mat4.multiply(m, Mat4Transform.scale(legWidth,legHeight,legDepth));
      m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
      TransformNode backRightLegTransform = new TransformNode("backrightleg transform", m);
        ModelNode backRightLegShape = new ModelNode("Cube(backrightLeg)", cube);

    NameNode frontRightLeg = new NameNode("frontrightleg");
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.translate((tableWidth*0.5f)-(legWidth*0.5f),0,(tableDepth*0.5f)-(legWidth*0.5f)));
      m = Mat4.multiply(m, Mat4Transform.rotateAroundX(180));
      m = Mat4.multiply(m, Mat4Transform.scale(legWidth,legHeight,legDepth));
      m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
      TransformNode frontRightLegTransform = new TransformNode("frontrightleg transform", m);
        ModelNode frontRightLegShape = new ModelNode("Cube(frontrightLeg)", cube);

    NameNode backLeftLeg = new NameNode("backleftleg");
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.translate((tableWidth*-0.5f)+(legWidth*0.5f),0,(tableDepth*-0.5f)+(legWidth*0.5f)));
        m = Mat4.multiply(m, Mat4Transform.rotateAroundX(180));
        m = Mat4.multiply(m, Mat4Transform.scale(legWidth,legHeight,legDepth));
        m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
        TransformNode backLeftLegTransform = new TransformNode("backleftleg transform", m);
          ModelNode backLeftLegShape = new ModelNode("Cube(backleftLeg)", cube);
  
    NameNode frontLeftLeg = new NameNode("frontleftleg");
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.translate((tableWidth*-0.5f)+(legWidth*0.5f),0,(tableDepth*0.5f)-(legWidth*0.5f)));
      m = Mat4.multiply(m, Mat4Transform.rotateAroundX(180));
      m = Mat4.multiply(m, Mat4Transform.scale(legWidth,legHeight,legDepth));
      m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
      TransformNode frontLeftLegTransform = new TransformNode("frontleftleg transform", m);
        ModelNode frontLeftLegShape = new ModelNode("Cube(frontleftLeg)", cube);

    NameNode box = new NameNode("box");
      m = Mat4.multiply(m, Mat4Transform.translate(tableWidth -0.5f , -(tableHeight + tableHeight * 0.5f), tableDepth - 0.5f));
      m = Mat4.multiply(m, Mat4Transform.scale(tableWidth * 0.75f, tableHeight * 0.5f, tableDepth * 0.75f));
      m = Mat4.multiply(m, Mat4Transform.translate(0,0,0));
      TransformNode boxTransform = new TransformNode("box transform", m);
      ModelNode boxShape = new ModelNode("Cube(box)", cube);

    TransformNode moveEgg = new TransformNode("Move egg to box", Mat4Transform.translate(0, tableHeight*2 + tableHeight * 0.5f, 0));

    jumpEgg = new TransformNode("translateY("+jumpHeight+")", Mat4Transform.translate(0, jumpHeight, 0));
    
    spinEgg = new TransformNode("rotateAroundY("+spinAngle+")", Mat4Transform.rotateAroundY(spinAngle));
    
    NameNode egg = new NameNode("egg");
      m = Mat4.multiply(m, Mat4Transform.scale(1f, 4f, 1f));
      m = Mat4.multiply(m, Mat4Transform.translate(0,0f,0));
      TransformNode eggTransform = new TransformNode("egg transform", m);
      ModelNode eggShape = new ModelNode("Sphere(egg)", sphere);

    //scene graph hierarchy for table and egg    

    tableRoot.addChild(tableMoveTranslate);
    tableMoveTranslate.addChild(tableTranslate);
      tableTranslate.addChild(desktop);
        desktop.addChild(desktopTransform);
          desktopTransform.addChild(desktopShape);
        desktop.addChild(backLeftLeg);
          backLeftLeg.addChild(backLeftLegTransform);
          backLeftLegTransform.addChild(backLeftLegShape);
        desktop.addChild(frontLeftLeg);
          frontLeftLeg.addChild(frontLeftLegTransform);
          frontLeftLegTransform.addChild(frontLeftLegShape);
        desktop.addChild(backRightLeg);
          backRightLeg.addChild(backRightLegTransform);
          backRightLegTransform.addChild(backRightLegShape);
        desktop.addChild(frontRightLeg);
          frontRightLeg.addChild(frontRightLegTransform);
          frontRightLegTransform.addChild(frontRightLegShape);
        desktop.addChild(box);
          box.addChild(boxTransform);
            boxTransform.addChild(boxShape);
          box.addChild(moveEgg);
            moveEgg.addChild(egg);
              egg.addChild(jumpEgg);
              jumpEgg.addChild(spinEgg);
                spinEgg.addChild(eggTransform);
                eggTransform.addChild(eggShape);

    tableRoot.update();

    //Scene graph for lamp1(on the right side)

    float baseWidth = 1f;
    float baseScale = 0.4f;
    float armScale = 0.3f;
    float armHeight = 2f;

    lamp1Root = new NameNode("lamp1");
    lamp1MoveTranslate = new TransformNode("lamp1 transform",Mat4Transform.translate(xPosition + 4f,0,0));

    TransformNode lamp1Translate = new TransformNode("lamp1 transform",Mat4Transform.translate(0, 0,0));

    NameNode lamp1base = new NameNode("lamp1base");
    m = new Mat4(1);
    m = Mat4Transform.scale(baseWidth, baseScale, baseScale);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode lamp1baseTransform = new TransformNode("lamp1base transform", m);
    ModelNode lamp1baseShape = new ModelNode("Cube(lamp1base)", cube);

    rotateLower1 = new TransformNode("rotateAroundZ("+rotateLowerAngle1+")", Mat4Transform.rotateAroundZ(rotateLowerAngle1));

    rotateLower1y = new TransformNode("rotateAroundY("+rotateLowerAngle1y+")", Mat4Transform.rotateAroundY(rotateLowerAngle1y));

    NameNode lamp1LowerArm = new NameNode("lamp1lowerarm");
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(0,baseScale,0));
    m = Mat4.multiply(m, Mat4Transform.scale(armScale, armHeight, armScale));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    
    TransformNode lamp1LowerArmTransform = new TransformNode("lamp1lowerarm transform", m);
    ModelNode lamp1LowerArmShape = new ModelNode("Sphere(lamp1lowerarm)", sphere);

    NameNode lamp1joint = new NameNode("lamp1joint");
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(0,((armHeight)+0.25f),0));
    m = Mat4.multiply(m, Mat4Transform.scale(0.5f, 0.5f, 0.5f));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    
    TransformNode lamp1jointTransform = new TransformNode("lamp1lowerarm transform", m);
    ModelNode lamp1jointShape = new ModelNode("Sphere(lamp1lowerarm)", sphere);
    
    NameNode lamp1tail = new NameNode("lamp1tail");
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(0.15f,armHeight + 0.5f,0));
    m = Mat4.multiply(m, Mat4Transform.rotateAroundZ(-60));
    m = Mat4.multiply(m, Mat4Transform.scale(armScale, armHeight/2, armScale));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode lamp1tailTransform = new TransformNode("lamp1tail transform", m);
    ModelNode lamp1tailShape = new ModelNode("Sphere(lamp1tail)", sphere);

    NameNode lamp1tailbush = new NameNode("lamp1tailbush");
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(armHeight/2 + 0.25f,armHeight + 0.75f,0));
    m = Mat4.multiply(m, Mat4Transform.scale(0.75f, 0.75f, 0.75f));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode lamp1tailbushTransform = new TransformNode("lamp1tail transform", m);
    ModelNode lamp1tailbushShape = new ModelNode("Sphere(lamp1tail)", sphere);

    NameNode lamp1UpperArm = new NameNode("lamp1UpperArm");
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(0,((armHeight)+0.5f),0));
    TransformNode lamp1UpperArmTranslate = new TransformNode("lamp1UpperArm transform", m);

    rotateUpper1 = new TransformNode("rotateAroundZ("+rotateUpperAngle1+")", Mat4Transform.rotateAroundZ(rotateUpperAngle1));

    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(armScale, armHeight, armScale));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));

    TransformNode lamp1UpperArmTransform = new TransformNode("lamp1UpperArm transform", m);
    ModelNode lamp1UpperArmShape = new ModelNode("Sphere(lamp1UpperArm)", sphere);
    
    rotateHead1 = new TransformNode("rotateAroundZ("+rotateHead1Angle+")", Mat4Transform.rotateAroundZ(rotateHead1Angle));

    NameNode lamp1head = new NameNode("lamp1head");
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(0,(armHeight),0));
    TransformNode lamp1headTranslate = new TransformNode("lamp1head translate", m);
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(baseWidth, baseScale, baseScale));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode lamp1headTransform = new TransformNode("lamp1head transform", m);
    ModelNode lamp1headShape = new ModelNode("Cube(lamp1head)", cube);


    NameNode lamp1horn1 = new NameNode("lamp1horn1");
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(0.1f,0.25f,0.1f));
    m = Mat4.multiply(m, Mat4Transform.rotateAroundZ(40));
    m = Mat4.multiply(m, Mat4Transform.scale(0.1f, 0.5f, 0.1f));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode lamp1horn1Transform = new TransformNode("lamp1horn1 transform", m);
    ModelNode lamp1horn1Shape = new ModelNode("Cube(lamp1horn1)", sphere);

    NameNode lamp1horn2 = new NameNode("lamp1horn1");
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(0.1f,0.25f,-0.1f));
    m = Mat4.multiply(m, Mat4Transform.rotateAroundZ(40));
    m = Mat4.multiply(m, Mat4Transform.scale(0.1f, 0.5f, 0.1f));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode lamp1horn2Transform = new TransformNode("lamp1horn2 transform", m);
    ModelNode lamp1horn2Shape = new ModelNode("Cube(lamp1horn2)", sphere);


    NameNode lamp1eye1 = new NameNode("lamp1eye1");
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(-0.5f,0.25f,0.2f));
    m = Mat4.multiply(m, Mat4Transform.scale(0.25f, 0.25f, 0.25f));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode lamp1eye1Transform = new TransformNode("lamp1eye1 transform", m);
    ModelNode lamp1eye1Shape = new ModelNode("Cube(lamp1eye1)", sphere_eyes);

    NameNode lamp1eye2 = new NameNode("lamp1eye2");
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(-0.5f,0.25f,-0.2f));
    m = Mat4.multiply(m, Mat4Transform.scale(0.25f, 0.25f, 0.25f));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode lamp1eye2Transform = new TransformNode("lamp1eye2 transform", m);
    ModelNode lamp1eye2Shape = new ModelNode("Cube(lamp1eye2)", sphere_eyes);

    NameNode lamp1light = new NameNode("lamp1Light");
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(-0.5f,0.25f,-0.2f));
    TransformNode lamp1lightTransform = new TransformNode("lamp1light transform", m);

    //scene graph hierarchy for lamp1

    lamp1Root.addChild(lamp1MoveTranslate);
    lamp1MoveTranslate.addChild(lamp1Translate);
      lamp1Translate.addChild(lamp1base);
        lamp1base.addChild(lamp1baseTransform);
          lamp1baseTransform.addChild(lamp1baseShape);
        lamp1base.addChild(rotateLower1y);
        rotateLower1y.addChild(rotateLower1);
         rotateLower1.addChild(lamp1LowerArm);
            lamp1LowerArm.addChild(lamp1LowerArmTransform);
              lamp1LowerArmTransform.addChild(lamp1LowerArmShape);
            lamp1LowerArm.addChild(lamp1joint);
              lamp1joint.addChild(lamp1jointTransform);  
                lamp1jointTransform.addChild(lamp1jointShape);
              lamp1joint.addChild(lamp1tail);
                lamp1tail.addChild(lamp1tailTransform);
                  lamp1tailTransform.addChild(lamp1tailShape);
                lamp1tail.addChild(lamp1tailbush);
                  lamp1tailbush.addChild(lamp1tailbushTransform);
                    lamp1tailbushTransform.addChild(lamp1tailbushShape);
              lamp1joint.addChild(lamp1UpperArmTranslate);
                lamp1UpperArmTranslate.addChild(rotateUpper1);
                  rotateUpper1.addChild(lamp1UpperArm);
                    lamp1UpperArm.addChild(lamp1UpperArmTransform);
                      lamp1UpperArmTransform.addChild(lamp1UpperArmShape);
                    lamp1UpperArm.addChild(lamp1headTranslate);
                    lamp1headTranslate.addChild(rotateHead1);
                      rotateHead1.addChild(lamp1head);
                      lamp1head.addChild(lamp1headTransform);
                        lamp1headTransform.addChild(lamp1headShape);
                      lamp1head.addChild(lamp1horn1);
                        lamp1horn1.addChild(lamp1horn1Transform);
                          lamp1horn1Transform.addChild(lamp1horn1Shape);
                      lamp1head.addChild(lamp1horn2);
                        lamp1horn2.addChild(lamp1horn2Transform);
                          lamp1horn2Transform.addChild(lamp1horn2Shape);
                      lamp1head.addChild(lamp1eye1);
                        lamp1eye1.addChild(lamp1eye1Transform);
                        lamp1eye1Transform.addChild(lamp1eye1Shape);
                      lamp1head.addChild(lamp1eye2);
                        lamp1eye2.addChild(lamp1eye2Transform);
                        lamp1eye2Transform.addChild(lamp1eye2Shape);
    lamp1Root.update();

    //scene graph for lamp 2 on the left
          
    lamp2Root = new NameNode("lamp2");
    lamp2MoveTranslate = new TransformNode("lamp2 transform",Mat4Transform.translate(xPosition - 4f,0,0));

    rotateLower2 = new TransformNode("rotateAroundZ("+rotateLowerAngle2+")", Mat4Transform.rotateAroundZ(rotateLowerAngle2));

    rotateLower2y = new TransformNode("rotateAroundY("+rotateLowerAngle2y+")", Mat4Transform.rotateAroundY(rotateLowerAngle2y));

    TransformNode lamp2Translate = new TransformNode("lamp2 transform",Mat4Transform.translate(0, 0,0));

    NameNode lamp2base = new NameNode("lamp2base");
    m = new Mat4(1);
    m = Mat4Transform.scale(baseWidth, baseScale, baseScale);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode lamp2baseTransform = new TransformNode("lamp2base transform", m);
    ModelNode lamp2baseShape = new ModelNode("Cube(lamp2base)", cube);

    NameNode lamp2LowerArm = new NameNode("lamp2lowerarm");
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(0,baseScale,0));
    m = Mat4.multiply(m, Mat4Transform.scale(armScale, armHeight, armScale));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    
    TransformNode lamp2LowerArmTransform = new TransformNode("lamp2lowerarm transform", m);
    ModelNode lamp2LowerArmShape = new ModelNode("Sphere(lamp2lowerarm)", sphere);

    NameNode lamp2joint = new NameNode("lamp2joint");
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(0,((armHeight)+0.25f),0));
    m = Mat4.multiply(m, Mat4Transform.scale(0.5f, 0.5f, 0.5f));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    
    TransformNode lamp2jointTransform = new TransformNode("lamp2lowerarm transform", m);
    ModelNode lamp2jointShape = new ModelNode("Sphere(lamp2lowerarm)", sphere);
    
    NameNode lamp2tail = new NameNode("lamp2tail");
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(0.25f,armHeight + 0.5f,0));
    m = Mat4.multiply(m, Mat4Transform.rotateAroundZ(70));
    m = Mat4.multiply(m, Mat4Transform.scale(armScale, armHeight, armScale));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode lamp2tailTransform = new TransformNode("lamp2tail transform", m);
    ModelNode lamp2tailShape = new ModelNode("Sphere(lamp2tail)", sphere);

    NameNode lamp2UpperArm = new NameNode("lamp2UpperArm");
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(0,((armHeight)+0.5f),0));

    TransformNode lamp2UpperArmTranslate = new TransformNode("lamp2UpperArm transform", m);

    rotateUpper2 = new TransformNode("rotateAroundZ("+rotateUpperAngle2+")", Mat4Transform.rotateAroundZ(rotateUpperAngle2));

    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(armScale, armHeight, armScale));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    
    TransformNode lamp2UpperArmTransform = new TransformNode("lamp2UpperArm transform", m);
    ModelNode lamp2UpperArmShape = new ModelNode("Sphere(lamp2UpperArm)", sphere);

    NameNode lamp2head = new NameNode("lamp2head");
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(0,(armHeight),0));
    TransformNode lamp2headTranslate = new TransformNode("lamp2head translate", m);

    rotateHead2 = new TransformNode("rotateAroundZ("+rotateHead2Angle+")", Mat4Transform.rotateAroundZ(rotateHead2Angle));
    
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(baseWidth, baseScale, baseScale));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode lamp2headTransform = new TransformNode("lamp2head transform", m);
    ModelNode lamp2headShape = new ModelNode("Cube(lamp2head)", cube);

    NameNode lamp2horn1 = new NameNode("lamp2horn1");
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(-0.1f,0.25f,0.1f));
    m = Mat4.multiply(m, Mat4Transform.rotateAroundZ(40));
    m = Mat4.multiply(m, Mat4Transform.scale(0.1f, 0.5f, 0.1f));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode lamp2horn1Transform = new TransformNode("lamp2horn1 transform", m);
    ModelNode lamp2horn1Shape = new ModelNode("Cube(lamp2horn1)", sphere);

    NameNode lamp2horn2 = new NameNode("lamp2horn2");
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(-0.1f,0.25f,-0.1f));
    m = Mat4.multiply(m, Mat4Transform.rotateAroundZ(40));
    m = Mat4.multiply(m, Mat4Transform.scale(0.1f, 0.5f, 0.1f));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode lamp2horn2Transform = new TransformNode("lamp2horn2 transform", m);
    ModelNode lamp2horn2Shape = new ModelNode("Cube(lamp2horn2)", sphere);

    NameNode lamp2horn3 = new NameNode("lamp2horn3");
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(0.1f,0.25f,0.1f));
    m = Mat4.multiply(m, Mat4Transform.rotateAroundZ(-40));
    m = Mat4.multiply(m, Mat4Transform.scale(0.15f, 0.7f, 0.15f));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode lamp2horn3Transform = new TransformNode("lamp2horn3 transform", m);
    ModelNode lamp2horn3Shape = new ModelNode("Cube(lamp2horn3)", sphere);


    NameNode lamp2eye1 = new NameNode("lamp2eye1");
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(0.5f,0.25f,0.2f));
    m = Mat4.multiply(m, Mat4Transform.scale(0.25f, 0.25f, 0.25f));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode lamp2eye1Transform = new TransformNode("lamp2eye1 transform", m);
    ModelNode lamp2eye1Shape = new ModelNode("Cube(lamp2eye1)", sphere_eyes);

    NameNode lamp2eye2 = new NameNode("lamp2eye2");
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(0.5f,0.25f,-0.2f));
    m = Mat4.multiply(m, Mat4Transform.scale(0.25f, 0.25f, 0.25f));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode lamp2eye2Transform = new TransformNode("lamp2eye2 transform", m);
    ModelNode lamp2eye2Shape = new ModelNode("Cube(lamp2eye2)", sphere_eyes);

    NameNode lamp2light = new NameNode("lamp2Light");
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(-0.5f,0.25f,-0.2f));
    m = Mat4.multiply(m, Mat4Transform.scale(0.25f, 0.25f, 0.25f));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));

    //scene graph hierarchy for lamp 2

    lamp2Root.addChild(lamp2MoveTranslate);
    lamp2MoveTranslate.addChild(lamp2Translate);
      lamp2Translate.addChild(lamp2base);
        lamp2base.addChild(lamp2baseTransform);
          lamp2baseTransform.addChild(lamp2baseShape);
        lamp2base.addChild(rotateLower2y);
        rotateLower2y.addChild(rotateLower2);
          rotateLower2.addChild(lamp2LowerArm);
            lamp2LowerArm.addChild(lamp2LowerArmTransform);
              lamp2LowerArmTransform.addChild(lamp2LowerArmShape);
            lamp2LowerArm.addChild(lamp2joint);
              lamp2joint.addChild(lamp2jointTransform);  
                lamp2jointTransform.addChild(lamp2jointShape);
              lamp2joint.addChild(lamp2tail);
                lamp2tail.addChild(lamp2tailTransform);
                  lamp2tailTransform.addChild(lamp2tailShape);
              lamp2joint.addChild(lamp2UpperArmTranslate);
                lamp2UpperArmTranslate.addChild(rotateUpper2);
                  rotateUpper2.addChild(lamp2UpperArm);
                    lamp2UpperArm.addChild(lamp2UpperArmTransform);
                      lamp2UpperArmTransform.addChild(lamp2UpperArmShape);
                    lamp2UpperArm.addChild(lamp2headTranslate);
                    lamp2headTranslate.addChild(rotateHead2);
                      rotateHead2.addChild(lamp2head);
                      lamp2head.addChild(lamp2headTransform);
                        lamp2headTransform.addChild(lamp2headShape);
                      lamp2head.addChild(lamp2horn1);
                        lamp2horn1.addChild(lamp2horn1Transform);
                          lamp2horn1Transform.addChild(lamp2horn1Shape);
                      lamp2head.addChild(lamp2horn2);
                        lamp2horn2.addChild(lamp2horn2Transform);
                          lamp2horn2Transform.addChild(lamp2horn2Shape);
                      lamp2head.addChild(lamp2horn3);
                        lamp2horn3.addChild(lamp2horn3Transform);
                          lamp2horn3Transform.addChild(lamp2horn3Shape);
                      lamp2head.addChild(lamp2eye1);
                        lamp2eye1.addChild(lamp2eye1Transform);
                        lamp2eye1Transform.addChild(lamp2eye1Shape);
                      lamp2head.addChild(lamp2eye2);
                        lamp2eye2.addChild(lamp2eye2Transform);
                        lamp2eye2Transform.addChild(lamp2eye2Shape);

    lamp2Root.update();
  }
 
  private void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    light.setPosition(0f,16f,0f);
    floor.render(gl); 
    windowframe1.render(gl);
    windowframe2.render(gl);
    windowframe3.render(gl);
    windowframe4.render(gl);
    leftWall.render(gl);
    rightWall.render(gl);
    sceneWall.render(gl);
    updateEggPos();
    updatelamppos();


    if (lightSwitch){
      Material material = new Material();
      material.setAmbient(0.5f, 0.5f, 0.5f);
      material.setDiffuse(0.8f, 0.8f, 0.8f);
      material.setSpecular(0.8f, 0.8f, 0.8f);

      light.setMaterial(material);
      light.render(gl);
    }

    else{
      Material material = new Material();
      material.setAmbient(0.3f, 0.3f, 0.3f);
      material.setDiffuse(0f, 0f, 0f);
      material.setSpecular(0f, 0f, 0f);
      light.setMaterial(material);
    }

    tableRoot.draw(gl);
    lamp1Root.draw(gl);
    lamp2Root.draw(gl);

  }


  //makes egg jump and spin  
  private void updateEggPos() {
    double elapsedTime = getSeconds()-startTime;
    float jumpHeight = 0.5f * (float)Math.sin(elapsedTime);
    if (jumpHeight < 0){
      jumpHeight = 0;
    }
    jumpEgg.setTransform(Mat4Transform.translate(0, jumpHeight, 0));
    
    float spinCheck = (float)Math.sin(elapsedTime);
    if (spinCheck > 0f){
      float spinAngle = 180f + 90f*(float)(elapsedTime);
      spinEgg.setTransform(Mat4Transform.rotateAroundY(spinAngle));
    }
    
    jumpEgg.update();
    spinEgg.update();
  }

  //animations for lamp poses

  private void updatelamppos(){
    if (targethead1 > currenthead1){
      currenthead1 += 1;
      rotateHead1.setTransform(Mat4Transform.rotateAroundZ(currenthead1));
      rotateHead1.update();
    }
    if (targetlower1 > currentlower1){
      currentlower1 += 1;
      rotateLower1.setTransform(Mat4Transform.rotateAroundZ(currentlower1));
      rotateLower1.update();
    }

    if (targetupper1 > currentupper1){
      currentupper1 += 1;
      rotateUpper1.setTransform(Mat4Transform.rotateAroundZ(currentupper1));
      rotateUpper1.update();
    }

    if (targethead1 < currenthead1){
      currenthead1 -= 1;
      rotateHead1.setTransform(Mat4Transform.rotateAroundZ(currenthead1));
      rotateHead1.update();
    }
    if (targetlower1 < currentlower1){
      currentlower1 -= 1;
      rotateLower1.setTransform(Mat4Transform.rotateAroundZ(currentlower1));
      rotateLower1.update();
    }

    if (targetupper1 < currentupper1){
      currentupper1 -= 1;
      rotateUpper1.setTransform(Mat4Transform.rotateAroundZ(currentupper1));
      rotateUpper1.update();
    }

    if (targethead2 > currenthead2){
      currenthead2 += 1;
      rotateHead2.setTransform(Mat4Transform.rotateAroundZ(currenthead2));
      rotateHead2.update();
    }
    if (targetlower2 > currentlower2){
      currentlower2 += 1;
      rotateLower2.setTransform(Mat4Transform.rotateAroundZ(currentlower2));
      rotateLower2.update();
    }

    if (targetupper2 > currentupper2){
      currentupper2 += 1;
      rotateUpper2.setTransform(Mat4Transform.rotateAroundZ(currentupper2));
      rotateUpper2.update();
    }

    if (targethead2 < currenthead2){
      currenthead2 -= 1;
      rotateHead2.setTransform(Mat4Transform.rotateAroundZ(currenthead2));
      rotateHead2.update();
    }
    if (targetlower2 < currentlower2){
      currentlower2 -= 1;
      rotateLower2.setTransform(Mat4Transform.rotateAroundZ(currentlower2));
      rotateLower2.update();
    }

    if (targetupper2 < currentupper2){
      currentupper2 -= 1;
      rotateUpper2.setTransform(Mat4Transform.rotateAroundZ(currentupper2));
      rotateUpper2.update();
    }

    if (targetlower2y < currentlower2y){
      currentlower2y -= 1;
      rotateLower2y.setTransform(Mat4Transform.rotateAroundY(currentlower2y));
      rotateLower2y.update();
    }

    if (targetlower1y < currentlower1y){
      currentlower1y -= 1;
      rotateLower1y.setTransform(Mat4Transform.rotateAroundY(currentlower1y));
      rotateLower1y.update();
    }

    if (targetlower2y > currentlower2y){
      currentlower2y += 1;
      rotateLower2y.setTransform(Mat4Transform.rotateAroundY(currentlower2y));
      rotateLower2y.update();
    }

    if (targetlower1y > currentlower1y){
      currentlower1y += 1;
      rotateLower1y.setTransform(Mat4Transform.rotateAroundY(currentlower1y));
      rotateLower1y.update();
    }
  }

  
  // ***************************************************
  /* TIME(adapted from labs)
   */ 
  
  private double startTime;
  
  private double getSeconds() {
    return System.currentTimeMillis()/1000.0;
  }
 
}