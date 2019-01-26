package com.reschikov.gdx.game.android;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class ObjectPool<T extends Poolable> implements Serializable{
    final List<T> activeList;
    final List<T> freeList;

    public List<T> getActiveList() {
        return activeList;
    }

    public List<T> getFreeList() {
        return freeList;
    }

    protected abstract T newObject();

    private void free(int index) {
        freeList.add(activeList.remove(index));
    }

    ObjectPool() {
        this.activeList = new ArrayList<>();
        this.freeList = new ArrayList<>();
    }

    void addObjectsToFreeList(int size) {
        for (int i = 0; i < size; i++) {
            freeList.add(newObject());
        }
    }

    T getActiveElement() {
        if (freeList.size() == 0) {
            freeList.add(newObject());
        }
        T temp = freeList.remove(freeList.size() - 1);
        activeList.add(temp);
        return temp;
    }

    void checkPool() {
        for (int i = activeList.size() - 1; i >= 0; i--) {
            if (!activeList.get(i).isActive()) {
                free(i);
            }
        }
    }

    void toLeaveLevel(){
        if (activeList.isEmpty()) return;
        for (int i = 0; i < activeList.size(); i++) {
            free(i);
        }
    }
}
