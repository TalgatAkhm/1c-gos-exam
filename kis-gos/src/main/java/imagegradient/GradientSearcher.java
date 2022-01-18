package imagegradient;

import image.Image;
import image.ImageMatrix;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.Rectangle;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Stack;
import java.util.function.Supplier;

/**
 * Searcher of the biggest rectangle witch contains gradient.
 */
public class GradientSearcher {
    private static final int MAX_VAL = Integer.MAX_VALUE;

    private final Image image;
    private final ImageMatrix<Rectangle> result;

    // Rectangle answer hist
    private final Stack<Rectangle> rectanglesHistory;
    private Rectangle biggest = new Rectangle(0, 0, 0, 0);

    public GradientSearcher(Image image) {
        this.image = image;
        result = new ImageMatrix<>(image.getWidth(), image.getHeight(), Rectangle.class);
        rectanglesHistory = new Stack<>();
    }

    /**
     * Stack of rectangles history. Deepest the older one.
     */
    public List<Rectangle> getRectanglesHistory() {
        return rectanglesHistory;
    }

    /**
     * Get the biggest rectangle.
     */
    public Rectangle getBiggest() {
        return rectanglesHistory.peek();
    }

    /**
     * Calculates rectangles witch contains gradient.
     */
    public void findGradient() {
        List<Deque<GradientValueAtAxisPixel>> leftRectBordersStates = new ArrayList<>();
        List<Deque<GradientValueAtAxisPixel>> rightRectBorderStates = new ArrayList<>();

        // Local states to calculate grad
        List<GradientState> yGradientState = new ArrayList<>();
        List<GradientState> xGradientState = new ArrayList<>();

        // Rectangles params
        List<Integer> leftUpXs = new ArrayList<>();
        List<Integer> widths = new ArrayList<>();

        List<Integer> totalXGradient = new ArrayList<>();

        for (int i = 0; i < image.getWidth(); i++) {
            yGradientState.add(new GradientState());
            xGradientState.add(new GradientState());

            leftUpXs.add(0);
            widths.add(0);

            totalXGradient.add(0);
            leftRectBordersStates.add(new ArrayDeque<>());
            rightRectBorderStates.add(new ArrayDeque<>());
        }

        // Gradients for each step
        Deque<Integer> yGradient = new ArrayDeque<>();
        Deque<Integer> xGradient = new ArrayDeque<>();

        Deque<GradientValueAtAxisPixel> curBorderUp = new ArrayDeque<>();

        for (int y = 0; y < image.getHeight(); y++) {
            yGradient.clear();
            if (y != 0) {
                for (int x = 0; x < image.getWidth(); x++) {
                    processNextStep(yGradientState, x, x, y - 1, y, y, yGradient);

                    totalXGradient.set(x, yGradient.size());
                }
            }

            curBorderUp.clear();
            xGradient.clear();
            for (int x = 0; x < image.getWidth(); x++) {
                if (x != 0) {
                    processNextStep(xGradientState, x - 1, x, y, y, x, xGradient);

                    totalXGradient.set(x, totalXGradient.get(x) + xGradient.size());
                } else
                    xGradientState.set(x, new GradientState());

                rollbackAndAdd(leftRectBordersStates, x, y, yGradientState, xGradientState.get(x));

                addNewValue(curBorderUp, x, yGradientState.get(x).size);
                GradientValueAtAxisPixel upPushMin = curBorderUp.getFirst();

                int leftUpPushBound = upPushMin.coord == x ? 0 : upPushMin.coord + 1;
                int leftPushBound = x - (MAX_VAL - leftRectBordersStates.get(x).getFirst().value) + 1;
                leftUpXs.set(x, Math.max(leftUpPushBound, leftPushBound));
            }

            GradientState right = new GradientState();
            curBorderUp.clear();
            xGradient.clear();
            for (int x = image.getWidth() - 1; x >= 0; x--) {
                if (x + 1 < image.getWidth()) {
                    int finalX = x;
                    GradientState finalRight = right;

                    right = processNextStep(right, null, x + 1, x, y, y, x, xGradient,
                            () -> xGradient.getFirst() >= finalX + finalRight.size - 1);

                    totalXGradient.set(x, totalXGradient.get(x) + xGradient.size());
                }

                rollbackAndAdd(rightRectBorderStates, x, y, yGradientState, right);

                addNewValue(curBorderUp, x, yGradientState.get(x).size);
                GradientValueAtAxisPixel min = curBorderUp.getFirst();

                int rightUpPushBound = min.coord == x ? image.getWidth() - 1 : min.coord - 1;
                int rightPushBound = x + (MAX_VAL - rightRectBorderStates.get(x).getFirst().value) - 1;
                widths.set(x, Math.min(rightUpPushBound, rightPushBound) - leftUpXs.get(x) + 1);
            }

            for (int x = 0; x < image.getWidth(); x++) {
                Rectangle rectangle = new Rectangle(leftUpXs.get(x), y - yGradientState.get(x).size + 1,
                        widths.get(x), yGradientState.get(x).size);

                result.setPixel(x, y, rectangle);
                if (area(biggest) < area(result.getPixel(x, y))) {
                    biggest = result.getPixel(x, y);
                    rectanglesHistory.push(biggest);
                }
            }
        }
    }

    private GradientState processNextStep(List<GradientState> states, int fromX, int toX, int fromY, int toY,
                                          int axisCoord, Deque<Integer> colorGradient) {

        return processNextStep(states.get(fromX), states, fromX, toX, fromY, toY, axisCoord, colorGradient,
                () -> colorGradient.getFirst() <= axisCoord - states.get(toX).size + 1);
    }

    private GradientState processNextStep(GradientState startState, List<GradientState> states,
                                          int fromX, int toX, int fromY, int toY, int axisCoord,
                                          Deque<Integer> colorGradient, Supplier<Boolean> condition) {

        Pair<GradientState, Boolean> next = GradientState.goNext(startState, image.getPixel(fromX, fromY),
                image.getPixel(toX, toY));
        boolean colorChanged = next.getRight();
        if (states != null)
            states.set(toX, next.getLeft());

        if (colorChanged)
            colorGradient.addLast(axisCoord);
        while (!colorGradient.isEmpty() && condition.get())
            colorGradient.pollFirst();
        return next.getLeft();
    }

    private static void rollbackAndAdd(List<Deque<GradientValueAtAxisPixel>> states, int i, int position,
                                       List<GradientState> gradientState, GradientState curState) {
        Deque<GradientValueAtAxisPixel> verticals = states.get(i);

        while(!verticals.isEmpty() && verticals.getFirst().coord < position - gradientState.get(i).size + 1)
            verticals.pollFirst();

        addNewValue(verticals, position, MAX_VAL - curState.size);
    }

    private static void addNewValue(Deque<GradientValueAtAxisPixel> verticals, int pos, int value) {
        while (!verticals.isEmpty() && verticals.getLast().value >= value)
            verticals.pollLast();

        verticals.addLast(new GradientValueAtAxisPixel(pos, value));
    }

    private static int area(Rectangle r) {
        return r.height * r.width;
    }

    private static final class GradientValueAtAxisPixel {
        private final int coord;
        private final int value;

        public GradientValueAtAxisPixel(int pos, int value) {
            this.coord = pos;
            this.value = value;
        }
    }
}
