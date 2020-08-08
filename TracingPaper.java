import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

import javafx.geometry.Dimension2D;
import javafx.geometry.Orientation;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;

public class TracingPaper extends Frame implements MouseListener, MouseMotionListener, WindowListener, ActionListener {
  int x, y, red, green, blue, clickCounter, polygonCounter;
  String str = "", clickedButton = "", cColorCode = "0", selectedShape = "";
  Label coordinates, mouse_activity;
  TextField functionName, colorR, colorG, colorB, stroke, startAngle, arcAngle;
  Checkbox fill, cCode, javaCode;
  ArrayList<Integer> xArr, yArr, xAll, yAll;
  int[] xSaved, ySaved;
  BufferedWriter writer;
  boolean templateBuild;

  public TracingPaper() {
    clickCounter = 0;
    xArr = new ArrayList<Integer>();
    yArr = new ArrayList<Integer>();
    xAll = new ArrayList<Integer>();
    yAll = new ArrayList<Integer>();
    
    // templateBuild checks if the code template is built on once when the BUILD
    // button is clicked
    templateBuild = false;
    // open empty file for writing;
    try {
      writer = new BufferedWriter(new FileWriter("./trace.txt", true)); // Set true for append mode
    } catch (final Exception exception) {
      System.out.println("Exception occoured while opening filse");
    }
    final Panel rightPanel = new Panel();
    final Panel footer = new Panel();
    final Panel shapes = new Panel();
    // button setup .---------------------------
    Button oval, rect, arc, line, poly, draw, save, clear, clearlast;
    oval = new Button("oval");
    rect = new Button("rect");
    arc = new Button("arc");
    line = new Button("line");
    poly = new Button("poly");
    draw = new Button("BUILD");
    // set = new Button("SET");
    save = new Button("SAVE");
    clear = new Button("ALL");
    clearlast= new Button("LAST");


    Button clearTraces = new Button("xT");
    Button pushToFile = new Button(">>");

    pushToFile.addActionListener(this);
    clearTraces.addActionListener(this);
    Button colorButtons[] = new Button[16]; // set color equavelnt to c color numbers
    
    int i = 0;
    Color javaColor[] = new Color[] { Color.BLACK, Color.BLUE, Color.GREEN, Color.CYAN, Color.RED, Color.MAGENTA,
        new Color(165, 42, 42), // BROWN
        Color.LIGHT_GRAY, Color.DARK_GRAY, new Color(173, 216, 230), // LIGHTBLUE
        new Color(144, 238, 144), // LIGHTGREEN
        new Color(224, 255, 255), // LIGHTCYAN
        new Color(225, 105, 97), // LIGHTRED
        new Color(231, 139, 231), // LIGHTMAGENTA
        Color.YELLOW, Color.WHITE }; // array containing colors equavalent to c colors

    for (Button b : colorButtons) {
      b = new Button(String.valueOf(i));
      b.setPreferredSize(new Dimension(14, 23)); // w=14,h=23
      b.addActionListener(this);
      b.setBackground(javaColor[i]);
      b.setForeground(javaColor[i++]);
      rightPanel.add(b);
    }

    oval.addActionListener(this);
    rect.addActionListener(this);
    arc.addActionListener(this);
    line.addActionListener(this);
    poly.addActionListener(this);
    draw.addActionListener(this);
    // set.addActionListener(this);
    save.addActionListener(this);
    clear.addActionListener(this);
    clearlast.addActionListener(this);

    // button setup ends---------------------------
    CheckboxGroup language = new CheckboxGroup();
    cCode = new Checkbox("C", language, false);
    javaCode = new Checkbox("JAVA", language, true);

    functionName = new TextField("Name");
    colorR = new TextField("0");
    colorG = new TextField("0");
    colorB = new TextField("0");
    stroke = new TextField("1");
    fill = new Checkbox("Fill", false);
    startAngle = new TextField("0");
    arcAngle = new TextField("0");

    footer.setPreferredSize(new Dimension(800, 30));
    shapes.setPreferredSize(new Dimension(800, 30));
    shapes.setBackground(Color.LIGHT_GRAY);
    rightPanel.setBackground(Color.LIGHT_GRAY);
    rightPanel.setPreferredSize(new Dimension(40, 800));

    shapes.add(fill);
    shapes.add(oval);
    shapes.add(rect);
    shapes.add(startAngle);
    shapes.add(arcAngle);
    shapes.add(arc);
    shapes.add(line);
    shapes.add(poly);
    shapes.add(new Label("||"));
    shapes.add(cCode);
    shapes.add(javaCode);
    shapes.add(new Label("function:"));
    shapes.add(functionName);
    shapes.add(draw);
    shapes.add(save);
    shapes.add(new Label("DROP INPUT:"));
    shapes.add(clear);
    shapes.add(clearlast);

    rightPanel.add(new Label("RGB:"));
    rightPanel.add(colorR);
    rightPanel.add(colorG);
    rightPanel.add(colorB);
    rightPanel.add(new Label("Bold"));
    rightPanel.add(stroke);
    rightPanel.add(clearTraces);
    //rightPanel.add(pushToFile);  -- for future implementation of writing selected line of code to new file

    coordinates = new Label("X,Y: . . . . .");
    mouse_activity = new Label("Mouse Activity: . . .");

    footer.setLayout(new BorderLayout());
    footer.add(coordinates, BorderLayout.WEST);
    footer.add(mouse_activity, BorderLayout.EAST);
    footer.add(new Label("Press Alt+F4 to QUIT || OutPut on trace.txt || click xT to clear traces"),
        BorderLayout.CENTER);
    footer.setBackground(Color.LIGHT_GRAY);
    this.add(footer, BorderLayout.SOUTH);
    this.add(shapes, BorderLayout.NORTH);
    this.add(rightPanel, BorderLayout.EAST);

    setUndecorated(true);
    setOpacity(0.660F);

    addMouseListener(this);
    addMouseMotionListener(this);
    addWindowListener(this);

    setSize(900, 600);
    setVisible(true);
  }

  // override ActionListener abstract methods
  public void actionPerformed(final ActionEvent e) {
    clickedButton = e.getActionCommand();
    System.out.println("clicked button:" + clickedButton);

    switch (clickedButton) {
      case "0":
      case "1":
      case "2":
      case "3":
      case "4":
      case "5":
      case "6":
      case "7":
      case "8":
      case "9":
      case "10":
      case "11":
      case "12":
      case "13":
      case "14":
      case "15":
      
        Button button = (Button) e.getSource();
        Color color = button.getBackground();
        colorR.setText(String.valueOf(color.getRed()));
        colorG.setText(String.valueOf(color.getGreen()));
        colorB.setText(String.valueOf(color.getBlue()));
        cColorCode = button.getLabel();
        break;
    }

    try {
      if (clickedButton == "BUILD" && cCode.getState()) {
        if (templateBuild) {
          writer.newLine();
          writer.write("void " + functionName.getText() + "(int x, int y){");
        } else {
          writer.newLine();
          writer.write("#include <graphics.h>");
          writer.newLine();
          writer.write("#include <conio.h> //for getch");
          writer.newLine();
          writer.write("#include <dos.h> //for delay");
          writer.newLine();
          writer.write("\nvoid main() { ");
          writer.newLine();
          writer.write("int gd = DETECT, gm;");
          writer.newLine();
          writer.write("initgraph(&gd, &gm, \"\"); //---specify BGI path as C:\\TC3\\BGI or C:\\tc\\bgi");
          writer.newLine();
          writer.write("getch();");
          writer.newLine();
          writer.write("closegraph(); ");
          writer.newLine();
          writer.write("}");
          writer.newLine();
          writer.write("//------------------- END OF C TEMPLATE--------------");

          writer.newLine();
          writer.write("void " + functionName.getText() + "(int x, int y){");
          templateBuild = true;
        }
      } else if (clickedButton == "BUILD" && javaCode.getState()) {
        if (templateBuild) {
          writer.newLine();
          writer.write("void " + functionName.getText() + "(int x, int y ,Graphics g){");
          writer.newLine();
          writer.write("Graphics2D g2D = (Graphics2D) g;");
        } else {
          writer.newLine();
          writer.write("import java.awt.*;");
          writer.newLine();
          writer.write("import javax.swing.JFrame;");
          writer.newLine();
          writer.write("\npublic class DisplayGraphics extends Canvas{");
          writer.newLine();
          writer.write("public void paint(Graphics g) {/*Call ur drawing methods here*/}");
          writer.newLine();
          writer.write("public static void main(String[] args) {");
          writer.newLine();
          writer.write("DisplayGraphics m=new DisplayGraphics();");
          writer.newLine();
          writer.write("JFrame f=new JFrame();");
          writer.newLine();
          writer.write(" f.add(m); f.setSize(600,600); f.setVisible(true);");
          writer.newLine();
          writer.write("}\n}");
          writer.newLine();
          writer.write("//------------------- END OF JAVA TEMPLATE--------------");

          writer.newLine();
          writer.write("void " + functionName.getText() + "(int x, int y ,Graphics g){");
          writer.newLine();
          writer.write("Graphics2D g2D = (Graphics2D) g;");
          templateBuild = true;
        }

      } else if (clickedButton == "poly" && !(fill.getState())) {

        canvasDrawingSetup(clickedButton);
        
        colorStrokeSetter();
        polygonSetup(false);
        clearLastInput();

      } else if (clickedButton == "poly" && fill.getState()) {
        canvasDrawingSetup(clickedButton);

        colorStrokeSetter();
        polygonSetup(true);
        clearLastInput();

      } else if (clickedButton == "oval" && !(fill.getState())) {

        canvasDrawingSetup(clickedButton);
        colorStrokeSetter();
        ovalArcSetup(false, false);
        clearLastInput();

      } else if (clickedButton == "oval" && fill.getState()) {
        canvasDrawingSetup(clickedButton);
        colorStrokeSetter();
        ovalArcSetup(true, false);
        clearLastInput();

      } else if (clickedButton == "rect" && !(fill.getState())) {
        canvasDrawingSetup(clickedButton);
        colorStrokeSetter();
        rectangleSetup(false);
        clearLastInput();

      } else if (clickedButton == "rect" && fill.getState()) {
        canvasDrawingSetup(clickedButton);
        colorStrokeSetter();
        rectangleSetup(true);
        clearLastInput();

      } else if (clickedButton == "line") {
        canvasDrawingSetup(clickedButton);
        colorStrokeSetter();
        lineSetup();
        clearLastInput();

      } else if (clickedButton == "arc" && !(fill.getState())) {
        canvasDrawingSetup(clickedButton);
        colorStrokeSetter();
        ovalArcSetup(false, true);
        clearLastInput();

      } else if (clickedButton == "arc" && fill.getState()) {
        canvasDrawingSetup(clickedButton);
        colorStrokeSetter();
        ovalArcSetup(true, true);
        clearLastInput();
      }

      if (clickedButton == "SAVE") {
        writer.newLine();
        writer.write("//}//----------- END SESSION -------------");
        writer.close();
        System.out.println("Changes to file have been saved");
        // open empty file for writing;
        try {
          writer = new BufferedWriter(new FileWriter("./trace.txt", true)); // Set true for append mode
          // Runtime.getRuntime().exec("more ./trace.txt");
        } catch (final Exception exception) {
          System.out.println("exception in opening file or launching cmd");
        }
      }
      if (clickedButton == "ALL") {
        clearLastInput();
      }
      if(clickedButton == "xT"){
        clearTracingHistory();
        xSaved = new int[0];
        ySaved = new int[0];
        selectedShape = null;
      }
      if(clickedButton == "LAST"){
        if(xArr.size() == 0 || yArr.size() ==0){
          System.out.println("No value to clear anymore");
          return;
        }
        xArr.remove(xArr.size()-1);
        yArr.remove(yArr.size()-1);
        System.out.println("removed last input cordinates");
      }

    } catch (final Exception exception) {
      System.out.println("IOException Occoured:" + exception);
    }
  }
  public void canvasDrawingSetup(String clickedButton){
    xSaved = new int[xArr.size()]; //converting xArr ArrayList to xSaved Array
    ySaved = new int[xArr.size()]; //converting xArr ArrayList to xSaved Array
    for(int i=0;i<xArr.size();i++){
      xSaved[i]=xArr.get(i);
      ySaved[i]=yArr.get(i);
    }
    selectedShape = clickedButton;
  }

  public void clearTracingHistory(){
    xAll = new ArrayList<Integer>();
    yAll = new ArrayList<Integer>();
    System.out.println("All Tracing Point history Cleared");
  }

  public void ovalArcSetup(boolean fill, boolean isArc) {
    try {
      final Integer xPoint = xArr.get(0);
      final Integer yPoint = yArr.get(0);
      final int width = Math.abs(xArr.get(1) - xPoint);
      final int height = Math.abs(yArr.get(1) - yPoint);

      String values = "";
      if (isArc) {
        try {
          if (javaCode.getState()) {
            final int startAng = Integer.parseInt(startAngle.getText());
            final int arcAng = Integer.parseInt(arcAngle.getText());
            values = xPoint + "+x," + yPoint + "+y," + width + "," + height + "," + startAng + "," + arcAng;
            writer.newLine();
            if (fill) {
              writer.write("g.fillArc(" + values + ");");
            } else {
              writer.write("g.drawArc(" + values + ");");
            }
          } else { // for c code
            final int startAng = Integer.parseInt(startAngle.getText());
            final int arcAng = Integer.parseInt(arcAngle.getText());

            int xCentre, yCentre, xRadius, yRadius, startingAngle, endingAngle;
            xCentre = xArr.get(1) - width / 2;
            yCentre = yArr.get(1) - height / 2;
            xRadius = width / 2;
            yRadius = height / 2;

            startingAngle = startAng;
            endingAngle = startAng + arcAng;

            values = xPoint + "+x," + yPoint + "+y," + startingAngle + "," + endingAngle + "," + xRadius + ","
                + yRadius;
            writer.newLine();
            writer.write("ellipse(" + values + ");");
          }
        } catch (Exception exception) {
          System.out.println("Start and End angles required for arc");
        }
      } else { // for oval

        if (javaCode.getState()) {
          values = xPoint + "+x," + yPoint + "+y," + width + "," + height;
          writer.newLine();
          if (fill) {
            writer.write("g.fillOval(" + values + ");");
          } else {
            writer.write("g.drawOval(" + values + ");");
          }
        } else { // for c code
          int xCentre, yCentre, xRadius, yRadius, startingAngle, endingAngle;
          xCentre = xArr.get(1) - width / 2;
          yCentre = yArr.get(1) - height / 2;
          xRadius = width / 2;
          yRadius = height / 2;

          if (fill) {
            values = xCentre + "+x," + yCentre + "+y," + xRadius + "," + yRadius;
            writer.newLine();
            writer.write("fillellipse(" + values + ");");
          } else {
            values = xCentre + "+x," + yCentre + "+y," + "0" + "," + "360" + "," + xRadius + "," + yRadius;
            writer.newLine();
            writer.write("ellipse(" + values + ");");
          }
        }

      }

    } catch (final Exception exception) {
      System.out.println("Error occoured while tracing arc or oval: " + exception);
    }
  }

  public void lineSetup() {
    try {

      for (int i = 0; i < xArr.size() - 1; i++) {
        writer.newLine();
        final int x0 = xArr.get(i);
        final int y0 = yArr.get(i);
        final int x1 = xArr.get(i + 1);
        final int y1 = yArr.get(i + 1);
        if (javaCode.getState())
          writer.write("g.drawLine(" + x0 + "," + y0 + "," + x1 + "," + y1 + ");");
        else
          writer.write("line(" + x0 + "," + y0 + "," + x1 + "," + y1 + ");");
      }

    } catch (final Exception exception) {
      System.out.println("Insufficient value of Line: " + exception);
    }
  }

  public void rectangleSetup(boolean fill) {
    try {

      final Integer xPoint1 = xArr.get(0);
      final Integer yPoint1 = yArr.get(0);
      final int width = Math.abs(xArr.get(1) - xPoint1);
      final int height = Math.abs(yArr.get(1) - yPoint1);
      String values = "";
      if (javaCode.getState()) {
        values = xPoint1 + "+x," + yPoint1 + "+y," + width + "," + height;

        writer.newLine();
        if (fill)
          writer.write("g.fillRect(" + values + ");");
        else
          writer.write("g.drawRect(" + values + ");");

      } else { // for c code
        values = xPoint1 + "+x," + yPoint1 + "+y," + (width + xPoint1) + "+x," + (height + yPoint1) + "+y";
        writer.newLine();
        if (fill) {
          writer.write("setfillstyle(1,4);");
          writer.newLine();
          writer.write("rectangle(" + values + ");");
          writer.newLine();
          writer.write("floodfill(" + (width + xPoint1 - (width / 2)) + "," + (height + yPoint1 - (height / 2)) + ","
              + cColorCode + ");");
        } else
          writer.write("rectangle(" + values + ");");

      }
    } catch (final Exception exception) {
      System.out.println("Insufficient value of Rectangle: " + exception);
    }
  }

  public void polygonSetup(boolean fill) {
    polygonCounter++;
    try {
      if (javaCode.getState()) {
        final StringBuilder xPoints = new StringBuilder("" + xArr);
        final StringBuilder yPoints = new StringBuilder("" + yArr);
        xPoints.deleteCharAt(0);
        xPoints.deleteCharAt(xPoints.length() - 1);
        yPoints.deleteCharAt(0);
        yPoints.deleteCharAt(yPoints.length() - 1);
        // Post-fixing +x and +y to each coordinate
        final String xValues = xPoints.toString().replace(",", "+x,");
        final String yValues = yPoints.toString().replace(",", "+y,");

        writer.newLine();
        writer.write("int xpoints"+polygonCounter+"[]={" + xValues + "+x};");
        writer.newLine();
        writer.write("int ypoints"+polygonCounter+"[]={" + yValues + "+y};");
        writer.newLine();
        writer.write("int points"+polygonCounter+"=" + yArr.size() + ";");
        writer.newLine();
        if (fill)
          writer.write("g.fillPolygon(xpoints"+polygonCounter+",ypoints"+polygonCounter+",points"+polygonCounter+");");
        else
          writer.write("g.drawPolygon(xpoints"+polygonCounter+",ypoints"+polygonCounter+",points"+polygonCounter+");");
      } else { // for c Code
        ArrayList<String> xyArr = new ArrayList<>();
        for (int i = 0; i < xArr.size(); i++) {
          xyArr.add(String.valueOf(xArr.get(i)) + "+x");
          xyArr.add(String.valueOf(yArr.get(i)) + "+y");
        }
        xyArr.add(String.valueOf(xArr.get(0)) + "+x");
        xyArr.add(String.valueOf(yArr.get(0)) + "+y");
        final StringBuilder xyPoints = new StringBuilder("" + xyArr);
        xyPoints.deleteCharAt(0);
        xyPoints.deleteCharAt(xyPoints.length() - 1);

        writer.newLine();
        writer.write("int points"+polygonCounter+"=" + (xyArr.size() / 2) + ";");
        writer.newLine();
        writer.write("int xyArray"+polygonCounter+"[]={" + xyPoints + "};");
        writer.newLine();
        if (fill)
          writer.write("fillpoly(points"+polygonCounter+",xyArray"+polygonCounter+");");
        else
          writer.write("drawpoly(points"+polygonCounter+",xyArray"+polygonCounter+");");
      }
    } catch (Exception e) {
      System.out.println("Exception occoured in Polygonsetup");
    }
  }

  public void colorStrokeSetter() {
    final String color = colorR.getText() + "," + colorG.getText() + "," + colorB.getText();
    try {
      writer.newLine();
      if (javaCode.getState()) {
        writer.write("g.setColor(new Color(" + color + "));");
        writer.newLine();
        writer.write("g2D.setStroke(new BasicStroke(" + stroke.getText() + "F));");
      } else {
        writer.write("setcolor(" + cColorCode + ");");
      }
    } catch (Exception e) {
      System.out.println("Exception while saving color and stroke");
    }
  }

  public void clearLastInput() {
    clickCounter = 0;
    clickedButton = "";
    xAll.addAll(xArr);
    yAll.addAll(yArr);
    xArr = new ArrayList<Integer>();
    yArr = new ArrayList<Integer>();
    System.out.println("xArray ,yArray, clickCounter, clickedButton have been cleared");
  }

  // override WindowListner seven abstract methods //withoud use
  public void windowClosing(final WindowEvent e) {
    try {
      writer.close();
    } catch (final IOException e1) {
      e1.printStackTrace();
    }
    dispose();
  }

  public void windowDeactivated(final WindowEvent e) {
  }

  public void windowActivated(final WindowEvent e) {
  }

  public void windowDeiconified(final WindowEvent e) {
  }

  public void windowIconified(final WindowEvent e) {
  }

  public void windowClosed(final WindowEvent e) {
  }

  public void windowOpened(final WindowEvent e) {
  }

  // override MouseListener five abstract methods
  public void mousePressed(final MouseEvent e) {
    x = e.getX();
    y = e.getY();
    str = "Mouse Pressed";
    coordinates.setText(x + "," + y);
    mouse_activity.setText(str);
    repaint();
  }

  public void mouseReleased(final MouseEvent e) {
    x = e.getX();
    y = e.getY();
    str = "Mouse Released";
    coordinates.setText(x + "," + y);
    mouse_activity.setText(str);
    repaint();
  }

  public void mouseClicked(final MouseEvent e) {
    clickCounter++;
    // System.out.println(e.getClickCount());
    x = e.getX();
    y = e.getY();
    str = "Mouse Clicked";
    coordinates.setText(x + "," + y);
    mouse_activity.setText(str);
    System.out.println("P" + clickCounter + "(" + x + "," + y + ")"); // fir printing the cordinates

    xArr.add(x);
    yArr.add(y);
    selectedShape = null;

    repaint();
  }

  public void mouseEntered(final MouseEvent e) {
    x = e.getX();
    y = e.getY();
    str = "Mouse Entered";
    coordinates.setText(x + "," + y);
    mouse_activity.setText(str);
    repaint();
  }

  public void mouseExited(final MouseEvent e) {
    System.out.println(clickCounter + ":" + xArr + ":" + yArr);
    x = e.getX();
    y = e.getY();
    str = "Mouse Exited";
    coordinates.setText(x + "," + y);
    mouse_activity.setText(str);
    repaint();
  }

  // override MouseMotionListener two abstract methods
  public void mouseMoved(final MouseEvent e) {
    x = e.getX();
    y = e.getY();
    str = "Mouse Moved";
    coordinates.setText(x + "," + y);
    mouse_activity.setText(str);
    repaint();
  }

  public void mouseDragged(final MouseEvent e) {
    x = e.getX();
    y = e.getY();

    str = "Mouse dragged";
    coordinates.setText(x + "," + y);
    mouse_activity.setText(str);
    repaint();
  }

  public void paint(final Graphics g) {
    g.setFont(new Font("Monospaced", Font.BOLD, 15));
    try {
      red = Integer.parseInt(colorR.getText());
      green = Integer.parseInt(colorG.getText());
      blue = Integer.parseInt(colorB.getText());
      g.setColor(new Color(red, green, blue));
      g.fillRect(x + 20, y + 20, 10, 10); // gives the bullet

      // set stroke widht
      final Graphics2D g2D = (Graphics2D) g;
      g2D.setStroke(new BasicStroke(Float.parseFloat(stroke.getText())));
      
      // For drawing the shapes on canvas
      if(selectedShape == "poly" && fill.getState()){
        g2D.fillPolygon(xSaved, ySaved, xSaved.length);
      }else if(selectedShape == "poly" && !fill.getState()){
        g2D.drawPolygon(xSaved, ySaved, xSaved.length);
      }else if(selectedShape == "oval" && fill.getState()){
        g2D.fillOval(xSaved[0], ySaved[0], xSaved[1]-xSaved[0], ySaved[1]-ySaved[0]);
      }else if(selectedShape == "oval" && !fill.getState()){
        g2D.drawOval(xSaved[0], ySaved[0], xSaved[1]-xSaved[0], ySaved[1]-ySaved[0]);
      }else if(selectedShape == "arc" && fill.getState()){
        int angle1=Integer.parseInt(startAngle.getText());
        int angle2=Integer.parseInt(arcAngle.getText());
        g2D.fillArc(xSaved[0], ySaved[0], xSaved[1]-xSaved[0], ySaved[1]-ySaved[0],angle1, angle2);
      }else if(selectedShape == "arc" && !fill.getState()){
        int angle1=Integer.parseInt(startAngle.getText());
        int angle2=Integer.parseInt(arcAngle.getText());
        g2D.drawArc(xSaved[0], ySaved[0], xSaved[1]-xSaved[0], ySaved[1]-ySaved[0],angle1,angle2);
      }else if(selectedShape == "rect" && fill.getState()){
        g2D.fillRect(xSaved[0], ySaved[0], xSaved[1]-xSaved[0], ySaved[1]-ySaved[0]);
      }else if(selectedShape == "rect" && !fill.getState()){
        g2D.drawRect(xSaved[0], ySaved[0], xSaved[1]-xSaved[0], ySaved[1]-ySaved[0]);
      }else if(selectedShape == "line"){
        g2D.drawLine(xSaved[0], ySaved[0], xSaved[1], ySaved[1]);
      }

      //setting the line width preview
      g.setColor(Color.BLACK);
      g2D.drawLine(20, 50, 50, 50);
      
      // Graphics.h in C window limitation
      if (cCode.getState()) {
        g.setColor(Color.BLUE);
        g2D.setStroke(new BasicStroke(2F));
        g.drawRect(20, 50, 600, 400);
        g.drawString("Draw within the BLUE Rectangle for C/C++", 200, 480);
        g.drawString("RGB values, stroke size, fillArc will not work", 200, 500);
      }

      // for creating tracing marks
      g2D.setStroke(new BasicStroke(1F));
      g.setColor(Color.RED);
      for (int i = 0; i < xArr.size(); i++) {
        g.drawOval(xArr.get(i) - 2, yArr.get(i) - 2, 3, 3);
      }

      if (xAll.size() > 0 && yAll.size() > 0) {
        g.setColor(Color.BLACK);
        for (int i = 0; i < xAll.size(); i++) {
          g.drawOval(xAll.get(i) - 2, yAll.get(i) - 2, 3, 3); // draw tracing history marks
          // if(i<xAll.size()-1) // draw tracing history lines
          // g.drawLine(xAll.get(i),yAll.get(i),xAll.get(i+1),yAll.get(i+1));
        }
      }

      // g.drawString(x + "," + y, x+10, y -10); // displays the x and y position
    } catch (final IllegalArgumentException e) {
      System.out.println("Invalid Color WARNING: RGB Ranges should be within 0-255");
    } catch (final Exception exception) {
      System.out.println("Some Exception:" + exception);
    }

  }

  public static void main(final String args[]) {
    new TracingPaper();
  }
}