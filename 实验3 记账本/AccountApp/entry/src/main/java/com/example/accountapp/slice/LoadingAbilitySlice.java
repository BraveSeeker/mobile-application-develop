package com.example.accountapp.slice;


import com.example.accountapp.LoadingPageAdapter;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Component;
import ohos.agp.components.PageSlider;
import ohos.global.systemres.ResourceTable;

/**
 * 引导页
 */
public class LoadingAbilitySlice extends BaseAbilitySlice {
    private PageSlider page;
    private LoadingPageAdapter adapter;
    private int[] images = {ResourceTable.Media_load_1, ResourceTable.Media_load_2, ResourceTable.Media_load_3, ResourceTable.Media_load_4};

    @Override
    protected void onStart(Intent intent) {
        super.onStart(intent);
        this.setUIContent(ResourceTable.Layout_ability_loading);

        page = (PageSlider) this.findComponentById(ResourceTable.Id_page);
        adapter = new LoadingPageAdapter(images, this);
        page.setProvider(adapter);

        page.addPageChangedListener(listener);
    }

    private PageSlider.PageChangedListener listener = new PageSlider.PageChangedListener() {
        @Override
        public void onPageSliding(int i, float v, int i1) {

        }

        @Override
        public void onPageSlideStateChanged(int i) {

        }

        @Override
        public void onPageChosen(int i) {
            if (i == images.length - 1) {
                page.setClickedListener(clickedListener);
            }
        }
    };

    private Component.ClickedListener clickedListener=new Component.ClickedListener() {
        @Override
        public void onClick(Component component) {
            present(new LoginAbilitySlice(), new Intent());
            terminate();
        }
    };
}