package imagegradient;

import image.Pixel;
import org.apache.commons.lang3.tuple.Pair;

public class GradientState {
    Pixel diff;
    int size;
    int rectWidth;
    int nextWidth;

    public GradientState(Pixel diff, int size, int rectWidth, int nextWidth) {
        this.diff = diff;
        this.size = size;
        this.rectWidth = rectWidth;
        this.nextWidth = nextWidth;
    }

    public GradientState() {
        diff = Pixel.black();
        size = 1;
        rectWidth = 1;
        nextWidth = 1;
    }

    /**
     * Compare two pixels and calculate state of gradient. Decide if the differential appears.
     */
    public static Pair<GradientState, Boolean> goNext(GradientState state, Pixel from, Pixel to) {
        Pixel diff = to.diff(from);

        boolean colorChanged = !diff.isBlackApproximately();

        if (colorChanged) {
            // diff of the diff
            if (!state.diff.equalsApproximately(diff)) {
                state.size = state.nextWidth + 1;
            } else {
                // increase the field
                state.size = (state.nextWidth < state.rectWidth) ? state.nextWidth * 2 : state.size + 1;
            }
            state.diff = diff;
            state.nextWidth = 1;
            state.rectWidth = state.nextWidth;
        } else {
            state.nextWidth++;

            if (state.nextWidth <= state.rectWidth) {
                state.size++;
            } else {
                state.size = Math.min(state.size + 1, state.nextWidth + state.size);
                state.rectWidth = state.nextWidth;
            }
        }
        return Pair.of(state, colorChanged);
    }
}
