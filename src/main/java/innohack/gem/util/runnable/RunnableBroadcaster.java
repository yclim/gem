package innohack.gem.util.runnable;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

public abstract class RunnableBroadcaster implements Runnable {
    public enum Event {
        COMPLETE
    }

    private float progress = 0;

    public float getProgress() {
        return progress;
    }

    private List<RunnableListener> listeners = new ArrayList<RunnableListener>();

    public void addListener(RunnableListener listener) {
        listeners.add(listener);
    }

    public void setListener(RunnableListener listener) {
        listeners = Lists.newArrayList(listener);
    }

    public void removeListener(RunnableListener listener) {
        listeners.remove(listener);
    }

    public void updateProgress(float progress) {
        this.progress = progress;
        for (RunnableListener listenr : listeners) {
            listenr.onProgressUpdate(progress, this);
        }
    }

    public void broadcast(Event e) {
        if (e.equals(Event.COMPLETE)) {
            for (RunnableListener listenr : listeners) {
                listenr.onProcessEnd(this);
            }
        }
    }
}
