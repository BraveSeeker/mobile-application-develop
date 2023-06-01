package com.example.accountapp;

//package com.example.bookkeepproject.adapter;

import ohos.aafwk.ability.AbilitySlice;
import ohos.agp.components.Component;
import ohos.agp.components.ComponentContainer;
import ohos.agp.components.Image;
import ohos.agp.components.PageSliderProvider;

public class LoadingPageAdapter extends PageSliderProvider {
    private int[] images;
    private AbilitySlice context;

    public LoadingPageAdapter(int[] images, AbilitySlice context) {
        this.images = images;
        this.context = context;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public Object createPageInContainer(ComponentContainer componentContainer, int i) {
        Image image=new Image(context);
        image.setScaleMode(Image.ScaleMode.STRETCH);
        ComponentContainer.LayoutConfig config=new ComponentContainer
                .LayoutConfig(ComponentContainer.LayoutConfig.MATCH_PARENT,
                ComponentContainer.LayoutConfig.MATCH_PARENT);
        image.setLayoutConfig(config);
        image.setPixelMap(images[i]);
        componentContainer.addComponent(image);
        return image;
    }

    @Override
    public void destroyPageFromContainer(ComponentContainer componentContainer, int i, Object o) {
        //滑出屏幕的组件进行移除
        componentContainer.removeComponent((Component) o);
    }

    @Override
    public boolean isPageMatchToObject(Component component, Object o) {
        //判断滑页上的每一页的组件和内容是否保持一致
        return true;
    }
}