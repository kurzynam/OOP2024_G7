import javafx.util.Builder;

public class TransformationDecorator extends ShapeDecorator{
    boolean translate;
    private Vec2 translateVector;
    private boolean rotate;
    private double rotateAngle;
    private Vec2 rotateCenter;
    private boolean scale;
    private Vec2 scaleVector;

    public TransformationDecorator(Shape shape, boolean translate, Vec2 translateVector, boolean rotate, double rotateAngle, Vec2 rotateCenter, boolean scale, Vec2 scaleVector) {
        super(shape);
        this.translate = translate;
        this.translateVector = translateVector;
        this.rotate = rotate;
        this.rotateAngle = rotateAngle;
        this.rotateCenter = rotateCenter;
        this.scale = scale;
        this.scaleVector = scaleVector;
    }

    public String toSvg(){
        StringBuilder sb = new StringBuilder(shape.toSvg());
        sb.append("transform=\"");
        if(rotate)
            sb.append(String.format(" rotate(%f %d %d) ", rotateAngle, rotateCenter.getX(), rotateCenter.getY()));
        if(translate)
            sb.append(String.format(" translate(%d %d) ", translateVector.getX(), translateVector.getY()));
        if (scale)
            sb.append(String.format(" scale(%d %d) ", scaleVector.getX(), scaleVector.getY()));
        sb.append("\"");
        return sb.toString();
    }

    public static class Builder{
        boolean translate;
        private Vec2 translateVector;
        private boolean rotate;
        private double rotateAngle;
        private Vec2 rotateCenter;
        private boolean scale;
        private Vec2 scaleVector;

        public Builder scale(Vec2 scaleVector){
            this.scaleVector = scaleVector;
            this.scale = true;
            return this;
        }

        public Builder rotate(double rotateAngle, Vec2 rotateCenter){
            this.rotateAngle = rotateAngle;
            this.rotateCenter = rotateCenter;
            this.rotate = true;
            return this;
        }

        public Builder translate(Vec2 translateVector){
            this.translateVector = translateVector;
            this.translate = true;
            return this;
        }

        public TransformationDecorator build(Shape shape){
            return new TransformationDecorator(shape, translate, translateVector, rotate, rotateAngle, rotateCenter, scale, scaleVector);
        }
    }

}
