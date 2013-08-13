package splat;

// Order is always pressed, released, clicked
public interface OnButtonListener {

    public void onButtonPressed(Splat splat);

    public void onButtonReleased(Splat splat);

    public void onButtonClicked(Splat splat);
}
