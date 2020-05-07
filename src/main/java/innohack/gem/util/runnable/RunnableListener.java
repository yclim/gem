package innohack.gem.util.runnable;

public interface RunnableListener {
    void onProcessEnd(RunnableBroadcaster runnable);

    void onProgressUpdate(float progress, RunnableBroadcaster runnable);
}
