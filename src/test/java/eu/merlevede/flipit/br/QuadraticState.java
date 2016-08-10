package eu.merlevede.flipit.br;

import java.text.DecimalFormat;
import java.util.Random;

/**
 * Created by jonat on 8/06/2016.
 */
class QuadraticState implements State<QuadraticState> {
    private final double a;
    private final double b;
    private final double c;
    private final double position;

    public QuadraticState(final double a, final double b, final double c, final double position) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.position = position;
    }

    public double getPosition() {
        return position;
    }

    private QuadraticState movePosition(final double diff) {
        return new QuadraticState(a, b, c, position + diff);
    }

    @Override
    public QuadraticState randomNeighbour(final Random random, final double distance) {
        if (random.nextBoolean()) {
            return movePosition(-distance);
        } else {
            return movePosition(distance);
        }
    }

    @Override
    public QuadraticState getThis() {
        return this;
    }

    public double calculateValue() {
        return a * position * position + b * position + c;
    }

//    @Override
//    public boolean equals(Object obkj) {
//        if (!(obj instanceof QuadraticState)) {
//            return false;
//        }
//        QuadraticState that = (QuadraticState)obj;
//        return (that.a == this.a
//                && that.b == this.b
//                && that.c == this.c
//                && that.position == this.position);
//    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        DecimalFormat df = new DecimalFormat("#.##");
        builder.append("Quadraticstate[a=");
        builder.append(df.format(a));
        builder.append(", b=");
        builder.append(df.format(b));
        builder.append(", c=");
        builder.append(df.format(c));
        builder.append(", position=");
        builder.append(df.format(position));
        builder.append(", value=");
        builder.append(df.format(calculateValue()));
        builder.append("]");
        return builder.toString();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        final QuadraticState that = (QuadraticState) o;

        if (Double.compare(that.a, a) != 0)
            return false;
        if (Double.compare(that.b, b) != 0)
            return false;
        if (Double.compare(that.c, c) != 0)
            return false;
        return Double.compare(that.position, position) == 0;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(a);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(b);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(c);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(position);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
