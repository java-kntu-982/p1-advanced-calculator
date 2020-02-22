package ir.ac.kntu;

public class Main {
    public static void main(String[] args) {
        runExample();// you should delete these and start your program here
    }

    /**
     * This is an Example for you to learn how to work with DrawUtil Class for
     * Drawing Graphs of Functions or Vectors
     */
    public static void runExample() {
        DrawUtil.addGraph(new double[]{0, -10}, new double[]{0, -10}, "Vector 1");
        DrawUtil.addGraph(new double[]{3, 2, 7}, new double[]{1, 3, 4}, "Line 1");

        double[] xsOfSine = new double[301];
        double[] ysOfSine = new double[301];
        double[] xsOfLog10 = new double[200];
        double[] ysOfLog10 = new double[200];
        int i = 0;
        for (double value = -10.0; value < 20.0; value += 0.1, i++) {
            xsOfSine[i] = value;
            ysOfSine[i] = Math.sin(value) - 1;
            if (value > 0) {
                xsOfLog10[i - 101] = value;
                ysOfLog10[i - 101] = Math.log10(value);
            }
        }
        DrawUtil.addGraph(xsOfLog10, ysOfLog10, "f(x) = Log10(x)");
        DrawUtil.addGraph(xsOfSine, ysOfSine, "f(x) = Sin(x)");
//        DrawUtil.addGraphAndDraw(xsOfSine, ysOfSine, "f(x) = Sin(x)");
        DrawUtil.draw();

    }
}
