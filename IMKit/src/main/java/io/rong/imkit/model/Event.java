//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.model;

public class Event {
    public Event() {
    }

    public static class ConnectEvent {
        private boolean isConnectSuccess;

        public ConnectEvent() {
        }

        public static Event.ConnectEvent obtain(boolean flag) {
            Event.ConnectEvent event = new Event.ConnectEvent();
            event.setConnectStatus(flag);
            return event;
        }

        public void setConnectStatus(boolean flag) {
            this.isConnectSuccess = flag;
        }

        public boolean getConnectStatus() {
            return this.isConnectSuccess;
        }
    }
}

