package com.example.test.rmtp.filterReuse;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.test.rmtp.filterReuse.record.ReuseBaseObjectFilterRecord;
import com.example.test.rmtp.refactor.FixSpriteGestureController;
import com.pedro.encoder.input.gl.SpriteGestureController;
import com.pedro.encoder.input.gl.render.filters.BaseFilterRender;
import com.pedro.encoder.input.gl.render.filters.NoFilterRender;
import com.pedro.encoder.input.gl.render.filters.object.BaseObjectFilterRender;
import com.pedro.encoder.utils.gl.TranslateTo;
import com.pedro.rtplibrary.rtmp.RtmpCamera2;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * useage:
 * 1.init
 * 2.addFilter
 * 3.update infos when info will be cleared,example prepareVideo...
 * 4.{@link #reusedFiltersByAddIndex()} after start stream
 */
public class FilterReusedManager {
    public static final String TAG=FilterReusedManager.class.getSimpleName();

    private RtmpCamera2 attachCamera;

    /**
     * key:filter key
     */
    private final Map<String, BaseObjectFilterReuse> reusedFilters=new HashMap<>(100);

    private final SpriteGestureController spriteGestureController = new FixSpriteGestureController();

    private String currentRenderKey;

    public void init(RtmpCamera2 attachCamera){
        this.attachCamera=attachCamera;
    }

    public int addFilter(BaseObjectFilterReuse addFilterReuse, String addKey){
        int addIndex=0;
        synchronized (reusedFilters){
            addIndex=reusedFilters.size();
            if(!reusedFilters.containsKey(addKey)){
                reusedFilters.put(addKey,addFilterReuse);
                addFilterReuse.setIdentifyKey(addKey);
                BaseFilterRender addRender=addFilterReuse.generateFilter(addFilterReuse.getFilterRecord());
                addFilterReuse.attachCameraAndRender(attachCamera,addRender,addIndex);
                attachSpriteGestureController(addRender, addKey);
                resetOperateFilter();
                Log.i(TAG, "addFilter: key "+addKey+" add index "+addIndex+" name "+addFilterReuse.getIdentifyKey());
            }else{
                Log.i(TAG, "addFilter: repeat key "+addKey+" name "+addFilterReuse.getIdentifyKey());
            }
        }
        return addIndex;
    }

    private void attachSpriteGestureController(BaseFilterRender attachRender, String currentRenderKey){
        if(attachRender instanceof BaseObjectFilterRender){
            spriteGestureController.setBaseObjectFilterRender((BaseObjectFilterRender)attachRender);
            spriteGestureController.setPreventMoveOutside(false);
            this.currentRenderKey=currentRenderKey;
        }
    }

    private void disattachSpriteGestureController(BaseFilterRender attachRender){
        if(spriteGestureController.getBaseObjectFilterRender() == attachRender){
            spriteGestureController.setBaseObjectFilterRender(null);
        }
    }

    public BaseObjectFilterReuse visibleFilter(String key,boolean visible){
        BaseObjectFilterReuse setupFilter=null;
        synchronized (reusedFilters){
            setupFilter=reusedFilters.get(key);
                if(setupFilter != null){
                    if(setupFilter.isVisible() != visible) {
                        setupFilter.setVisible(visible);
                        if (visible) {
                            setupFilter.reuse(attachCamera);
                        } else {
                            updateFilterInfo(key);
                            attachCamera.getGlInterface().setFilter(setupFilter.getFilterRecord().getReuseIndex(), new NoFilterRender());
                            disattachSpriteGestureController(setupFilter.getReusedFilter());
                            if (setupFilter.getReusedFilter() instanceof BaseObjectFilterRender) {
                                BaseObjectFilterRender setupRender = (BaseObjectFilterRender) setupFilter.getReusedFilter();
                            }
                        }
                    }
                }
                return setupFilter;
        }
    }

    public BaseObjectFilterReuse deprecatedFilter(String key){
        BaseObjectFilterReuse deprecatedFilter=null;
        String replaceKey;
        synchronized (reusedFilters){
            deprecatedFilter=reusedFilters.get(key);
            if(deprecatedFilter != null){
                deprecatedFilter.setDeprecated(true);
                deprecatedFilter.setReusedFilter(new NoFilterRender());

                attachCamera.getGlInterface().setFilter(deprecatedFilter.getFilterRecord().getReuseIndex(),deprecatedFilter.getReusedFilter());
                replaceKey=key+"_"+ Calendar.getInstance().getTimeInMillis();
//                replace original key
                reusedFilters.put(replaceKey,deprecatedFilter);
                reusedFilters.remove(key);
                deprecatedFilter.release();
            }
        }
        return deprecatedFilter;
    }

    public BaseObjectFilterReuse fetchFilter(String key){
        synchronized (reusedFilters){
            return reusedFilters.get(key);
        }
    }

    private void updateFilterInfo(String key){
        BaseObjectFilterReuse updatFilter;
        synchronized (reusedFilters){
            updatFilter=reusedFilters.get(key);
            if(updatFilter != null){
                updatFilter.getFilterRecord().updateInformation(updatFilter.getReusedFilter(), updatFilter.isVisible());
            }
        }
    }

    private List<String> sortAddIndexKeys(){
        synchronized (reusedFilters) {
            List<String> sortAddIndexKeys = new ArrayList<>(reusedFilters.size());
            Map<Integer,String> sortAddIndexMap=new TreeMap<>();
            BaseObjectFilterReuse baseObjectFilterReuse;
            for(String reuseKey:reusedFilters.keySet()){
                baseObjectFilterReuse=reusedFilters.get(reuseKey);
                sortAddIndexMap.put(baseObjectFilterReuse.getFilterRecord().getAddIndex(),reuseKey);
            }
            sortAddIndexKeys=new ArrayList<>(sortAddIndexMap.values());
            return sortAddIndexKeys;
        }
    }

    public void reusedFiltersByAddIndex(){
        List<String> invalidFilterKeys=new ArrayList<>(reusedFilters.size());
        synchronized (reusedFilters){
            List<String> sortAddIndexKeys=sortAddIndexKeys();
            int reuseIndex=-1;
            BaseObjectFilterReuse reusedFilter;
            for(String reuseKey:sortAddIndexKeys){
                reusedFilter=reusedFilters.get(reuseKey);
                if(reusedFilter != null) {
                    if (reusedFilter.isDeprecated()) {
                        invalidFilterKeys.add(reuseKey);
                    } else {
                        reuseIndex++;
                        reusedFilter.getFilterRecord().setReuseIndex(reuseIndex);
                        if(reusedFilter.isVisible()) {
                            reusedFilter.reuse(attachCamera);
                        }
                    }
                }
            }
            for(String removeKey:invalidFilterKeys){
                reusedFilters.remove(removeKey);
            }
        }
    }

    private BaseObjectFilterRender fetchOnTouchFilter(View v, MotionEvent event) {
        BaseObjectFilterRender touchFilter = null;
        BaseObjectFilterRender lastFilter = spriteGestureController.getBaseObjectFilterRender();
        if (lastFilter != null) {
            if (spriteGestureController.spriteTouched(v, event)) {
                touchFilter = lastFilter;
                return touchFilter;
            }
        }

        List<String> sortAddIndexKeys = sortAddIndexKeys();
        synchronized (reusedFilters) {
            BaseObjectFilterReuse filterReuse;
            BaseObjectFilterRender filterRender;
            for (String filterKey : sortAddIndexKeys) {
                filterReuse = reusedFilters.get(filterKey);
                if (filterReuse.getReusedFilter() instanceof BaseObjectFilterRender) {
                    if (filterReuse.canOperate()) {
                        filterRender = (BaseObjectFilterRender) filterReuse.getReusedFilter();
                        attachSpriteGestureController(filterRender, filterKey);
                        if (spriteGestureController.spriteTouched(v, event)) {
                            touchFilter = filterRender;
                            break;
                        }
                    }
                }
            }
        }
        return touchFilter;
    }

    public boolean dispatchOnTouch(View v, MotionEvent event){
        BaseObjectFilterRender matchFilter=fetchOnTouchFilter(v,event);
        if(matchFilter != null) {
            attachSpriteGestureController(matchFilter, currentRenderKey);
            ReuseBaseObjectFilterRecord matchFilterRecord=fetchFilter(currentRenderKey).getFilterRecord();
            if (spriteGestureController.spriteTouched(v, event)) {
                if(matchFilterRecord.isGestureMove()) {
                    spriteGestureController.moveSprite(v, event);
                    updateFilterInfo(currentRenderKey);
                }
                if(matchFilterRecord.isGestureScale()) {
                    spriteGestureController.scaleSprite(event);
                    updateFilterInfo(currentRenderKey);
                }
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param filterKey
     * @param scaleX setup 1.1 equals original width * 1.1
     * @param scaleY
     */
    public void setScale(String filterKey,float scaleX, float scaleY) {
        BaseObjectFilterReuse baseObjectFilterReuse=fetchFilter(filterKey);
        if(baseObjectFilterReuse != null){
            baseObjectFilterReuse.setScale(scaleX,scaleY);
        }
    }

    public void setPosition(String filterKey,float x, float y) {
        BaseObjectFilterReuse baseObjectFilterReuse=fetchFilter(filterKey);
        if(baseObjectFilterReuse != null){
            baseObjectFilterReuse.setPosition(x,y);
        }
    }

    public void setPosition(String filterKey,TranslateTo positionTo) {
        BaseObjectFilterReuse baseObjectFilterReuse=fetchFilter(filterKey);
        if(baseObjectFilterReuse != null){
            baseObjectFilterReuse.setPosition(positionTo);
        }
    }

    public void resetOperateFilter(){
        currentRenderKey="";
        spriteGestureController.setBaseObjectFilterRender(null);
    }

    public void destory(){
        synchronized (reusedFilters){
            for(BaseObjectFilterReuse relaseFilterReuse:reusedFilters.values()){
                relaseFilterReuse.getReusedFilter().release();
                relaseFilterReuse.getFilterRecord().release();
            }
            reusedFilters.clear();
        }
    }
}
