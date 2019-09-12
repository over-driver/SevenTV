package com.seventv.model.database;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import com.seventv.SevenTVApplication;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public abstract class DbList<E> implements List<E> {

    protected Map<Integer, E> cache = new HashMap<>();
    protected int mSizeTotal = -1;
    protected RecyclerView.Adapter mAdapter;

    protected abstract int getSizeTotal();

    public void destroy(){
        SevenTVApplication.DB_HELPER.removeDbList(this);
        cache.clear();
    }

    public void deleteCache(){
        cache.clear();
        mSizeTotal = -1;
        if(mAdapter != null){
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public int size() {
        if(mSizeTotal < 0){
            mSizeTotal = getSizeTotal();
        }
        return mSizeTotal;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(@Nullable Object o) {
        return false;
    }

    @NonNull
    @Override
    public Iterator iterator() {
        return null;
    }

    @Nullable
    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public Object[] toArray(@Nullable Object[] a) {
        return new Object[0];
    }

    @Override
    public boolean add(Object o) {
        return false;
    }

    @Override
    public boolean remove(@Nullable Object o) {
        return false;
    }

    @Override
    public boolean containsAll(@NonNull Collection c) {
        return false;
    }

    @Override
    public boolean addAll(@NonNull Collection c) {
        return false;
    }

    @Override
    public boolean addAll(int index, @NonNull Collection c) {
        return false;
    }

    @Override
    public boolean removeAll(@NonNull Collection c) {
        return false;
    }

    @Override
    public boolean retainAll(@NonNull Collection c) {
        return false;
    }

    @Override
    public void clear() {

    }

    protected abstract List<E> getCache(int offset, int limit);

    @Override
    public E get(int index) {
        if(!cache.containsKey(index)){
            int offset = (index - 10 < 0) ? 0 : (index - 10);
            List<E> newData = getCache(offset, 20);
            for(E data : newData) {
                cache.put(offset, data);
                offset++;
            }
        }
        return cache.get(index);
    }

    @Override
    public E set(int index, E element) {
        return null;
    }

    @Override
    public void add(int index, E element) {

    }

    @Override
    public E remove(int index) {
        return null;
    }

    @Override
    public int indexOf(@Nullable Object o) {
        return 0;
    }

    @Override
    public int lastIndexOf(@Nullable Object o) {
        return 0;
    }

    @NonNull
    @Override
    public ListIterator listIterator() {
        return null;
    }

    @NonNull
    @Override
    public ListIterator listIterator(int index) {
        return null;
    }

    @NonNull
    @Override
    public List subList(int fromIndex, int toIndex) {
        return null;
    }
}
