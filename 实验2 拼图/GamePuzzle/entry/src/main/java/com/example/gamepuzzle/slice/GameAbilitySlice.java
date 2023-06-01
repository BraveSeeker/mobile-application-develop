package com.example.gamepuzzle.slice;

import com.example.gamepuzzle.ImageSplit;
import com.example.gamepuzzle.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.agp.animation.AnimatorGroup;
import ohos.agp.animation.AnimatorProperty;
import ohos.agp.components.*;
import ohos.agp.components.element.ShapeElement;
import ohos.agp.utils.Color;
import ohos.agp.utils.LayoutAlignment;
import ohos.agp.utils.TextAlignment;
import ohos.agp.window.dialog.CommonDialog;
import ohos.agp.window.dialog.IDialog;
import ohos.data.rdb.ValuesBucket;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.media.image.ImagePacker;
import ohos.media.image.ImageSource;
import ohos.media.image.PixelMap;
import ohos.media.photokit.metadata.AVStorage;
import ohos.utils.net.Uri;

import static ohos.agp.components.ComponentContainer.LayoutConfig.MATCH_CONTENT;

import java.io.*;
import java.util.Collections;
import java.util.Vector;

public class GameAbilitySlice extends AbilitySlice {
    private static final HiLogLabel LABEL_LOG = new HiLogLabel(HiLog.LOG_APP, 0x00201, "XLC");
    //组件
    private final Vector<Image> imagesBlockList = new Vector<Image>();  //拼图块
    private Image curBlockImage;
    private TickTimer tickTimer;  //计时器
    private Image present_image;  //预览图
    private boolean isChangeSize = false; // 是否改变难度b
    private TableLayout secondLayout;
    private long startTime, currentTime;  // 开始时间
    private Text countText_count; // 点击次数
    private int picLen = 3, clickCount = 0;  //拼图行数、列数
    private int phone_width, phone_height;
    private int click_index_in_list = -1, blank_index_in_list = -1;
    private boolean isRunning = false, isMoving = false, isPicChange = false;
    private Vector<PixelMap> imgBlockVector;  // 记录正确拼图块顺序
    private int click_pos;
    private int blank_pos;
    private int blank_pos_row;
    private int blank_pos_col;
    private int playing_image_id; // 当前游戏的图片
    private final int imgRequestCode = 0;

    @Override
    public void onStart(Intent intent) {
        phone_width = AttrHelper.vp2px(getContext().getResourceManager().getDeviceCapability().width, this);
        phone_height = AttrHelper.vp2px(getContext().getResourceManager().getDeviceCapability().height, this);
        playing_image_id = intent.getIntParam("playing_image_id", ResourceTable.Media_pic0);

        blank_pos = picLen * picLen - 1;
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_GameAbility);
        initLayout();
        initParams();
        initImages();
        initAlbum();
        HiLog.debug(LABEL_LOG, "开始游戏");
        HiLog.debug(LABEL_LOG, "----imagesBlockList  ID: %{public}d, %{public}d, %{public}d, %{public}d", imagesBlockList.get(0).getId(), imagesBlockList.get(1).getId(), imagesBlockList.get(2).getId(), imagesBlockList.get(3).getId());
        HiLog.debug(LABEL_LOG, "----imagesBlockList TAG: %{public}d, %{public}d, %{public}d, %{public}d", imagesBlockList.get(0).getTag(), imagesBlockList.get(1).getTag(), imagesBlockList.get(2).getTag(), imagesBlockList.get(3).getTag());
    }

    private void initAlbum() {
        Vector<PixelMap> image_list = new Vector<PixelMap>(); // 储存手机中的图片
        int[] imageResources = new int[]{
                ResourceTable.Media_pic0,
                ResourceTable.Media_pic1,
                ResourceTable.Media_pic2,
                ResourceTable.Media_pic3,
                ResourceTable.Media_pic4,
        };
        for (int imageResource : imageResources) {
            image_list.add(ImageSplit.getPixelMap(getContext(), imageResource));
        }
//        for (int i = 0; i < image_list.size(); i++) {
//            saveImageToLibrary(String.valueOf(i), image_list.get(i));
//        }
    }

    private void initImages() {
        HiLog.debug(LABEL_LOG, "执行: initImages()之前, 切割图片 picLen = %{public}d", picLen);

        PixelMap p;
        if (isPicChange)
            p = present_image.getPixelMap();
        else
            p = ImageSplit.getPixelMap(getContext(), playing_image_id);

        this.present_image.setPixelMap(p);// 当前完整图片
        imgBlockVector = ImageSplit.Split(getContext(), p, picLen);

        initBlockImages();
        HiLog.debug(LABEL_LOG, "执行: initImages()之后, 切割图片 picLen = %{public}d， 切割后imgBlockVector.size() = %{public}d, imagesBlockList.size() = %{public}d ", picLen, imgBlockVector.size(), imagesBlockList.size());
    }

    //初始化布局
    public void initLayout() {
        int textSize = (int) (this.phone_width * 0.06);

        //主布局
        DirectionalLayout mainLayout = new DirectionalLayout(this);
        mainLayout.setOrientation(Component.VERTICAL);
        mainLayout.setWidth(ComponentContainer.LayoutConfig.MATCH_PARENT);
        mainLayout.setHeight(ComponentContainer.LayoutConfig.MATCH_PARENT);
        mainLayout.setAlignment(1);

        //第一部分，图片预览，计时器，StartButton， ResetButton，ChangeModelButton
        //添加计时器
        DirectionalLayout timer = new DirectionalLayout(this);
        timer.setWidth(ComponentContainer.LayoutConfig.MATCH_CONTENT);
        timer.setHeight(ComponentContainer.LayoutConfig.MATCH_CONTENT);
        timer.setOrientation(Component.HORIZONTAL);
        timer.setMarginTop(80);
        //text
        Text t = new Text(this);
        t.setWidth(ComponentContainer.LayoutConfig.MATCH_CONTENT);
        t.setHeight(ComponentContainer.LayoutConfig.MATCH_CONTENT);
        t.setText("用时: ");
        t.setTextSize(textSize);
        timer.addComponent(t);

        this.tickTimer = new TickTimer(this);
        this.tickTimer.setWidth(ComponentContainer.LayoutConfig.MATCH_CONTENT);
        this.tickTimer.setHeight(ComponentContainer.LayoutConfig.MATCH_CONTENT);
        this.tickTimer.setFormat("mm:ss");
        this.tickTimer.setCountDown(false);
        this.tickTimer.setTextSize(textSize);
        timer.addComponent(tickTimer);

        Text countText_text = new Text(this);
        countText_text.setWidth(ComponentContainer.LayoutConfig.MATCH_CONTENT);
        countText_text.setHeight(ComponentContainer.LayoutConfig.MATCH_CONTENT);
        countText_text.setText("   点击步数: ");
        countText_text.setTextSize(textSize);

        countText_count = new Text(this);
        countText_count.setWidth(ComponentContainer.LayoutConfig.MATCH_CONTENT);
        countText_count.setHeight(ComponentContainer.LayoutConfig.MATCH_CONTENT);
        countText_count.setText("0");
        countText_count.setTextSize(textSize);

        timer.addComponent(countText_text);
        timer.addComponent(countText_count);
        mainLayout.addComponent(timer);

        //第二部分，拼图块放置


        secondLayout = new TableLayout(this);
        secondLayout.setOrientation(Component.HORIZONTAL);
        secondLayout.setWidth(ComponentContainer.LayoutConfig.MATCH_CONTENT);
        secondLayout.setHeight(ComponentContainer.LayoutConfig.MATCH_CONTENT);
        secondLayout.setMarginTop(80);


//        initBlockImages();

        mainLayout.addComponent(secondLayout);

        // 难度选择
        RadioContainer radioContainer = new RadioContainer(this);
        radioContainer.setOrientation(Component.HORIZONTAL);
        radioContainer.setWidth(MATCH_CONTENT);
        radioContainer.setHeight(MATCH_CONTENT);
        radioContainer.setMarginTop(40);
        String[] model = new String[]{"2x2", "3x3", "4x4", "5x5"};
        for (String s : model) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(s);
            radioButton.setTag(s);
            radioButton.setTextSize(60);
            radioButton.setClickedListener(component -> ChangeModel(component.getTag()));
            radioContainer.addComponent(radioButton);
        }
        radioContainer.mark(1);
        mainLayout.addComponent(radioContainer);


        //基础信息
        DirectionalLayout firstLayout = new DirectionalLayout(this);
        firstLayout.setWidth((int) (phone_width * 0.8));
        firstLayout.setHeight(ComponentContainer.LayoutConfig.MATCH_CONTENT);
        firstLayout.setOrientation(Component.HORIZONTAL);
        firstLayout.setAlignment(1);
        firstLayout.setMarginTop(50);

        //添加计时器，StartButton， ResetButton
        DirectionalLayout btnDirectionLayout = new DirectionalLayout(getContext());
        btnDirectionLayout.setWidth(MATCH_CONTENT);
        btnDirectionLayout.setHeight(MATCH_CONTENT);
        btnDirectionLayout.setOrientation(Component.HORIZONTAL);
        btnDirectionLayout.setAlignment(LayoutAlignment.HORIZONTAL_CENTER);
        btnDirectionLayout.setMarginTop(20);

        //添加StartButton
        //重置游戏按钮
        Button startBtn = new Button(this);
        startBtn.setWidth(MATCH_CONTENT);
        startBtn.setHeight(MATCH_CONTENT);
        startBtn.setText("重新打乱");
        startBtn.setTextAlignment(TextAlignment.CENTER);
        startBtn.setTextSize(textSize);
        startBtn.setAlpha(0.5f);
        startBtn.setHintColor(new Color(ResourceTable.Color_btnColor));
        startBtn.setBackground(new ShapeElement(getContext(), ResourceTable.Graphic_btnbackground));
        startBtn.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                StartGame();
            }
        });
        btnDirectionLayout.addComponent(startBtn);

        //添加ResetButton
        Button resetBtn = new Button(this);
        resetBtn.setWidth(MATCH_CONTENT);
        resetBtn.setHeight(MATCH_CONTENT);
        resetBtn.setText("更改图片");
        resetBtn.setTextSize(textSize);
        resetBtn.setHintColor(new Color(ResourceTable.Color_btnColor));
        resetBtn.setTextAlignment(TextAlignment.CENTER);
        resetBtn.setAlpha(0.5f);
        resetBtn.setMarginLeft(100);
        resetBtn.setBackground(new ShapeElement(getContext(), ResourceTable.Graphic_btnbackground));
        resetBtn.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                changeImage();
            }
        });
        btnDirectionLayout.addComponent(resetBtn);

        firstLayout.addComponent(btnDirectionLayout);
        mainLayout.addComponent(firstLayout);

        //添加预览图
        this.present_image = new Image(this);
        this.present_image.setWidth((int) (phone_width / 2 * 0.8));
        this.present_image.setHeight((int) (phone_width / 2 * 0.8));
        this.present_image.setScaleMode(Image.ScaleMode.STRETCH);
        this.present_image.setCornerRadius((float) 40);
        this.present_image.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                changeImage();
            }
        });
        mainLayout.addComponent(this.present_image);
        DirectionalLayout total = (DirectionalLayout) findComponentById(ResourceTable.Id_backgroundLayout);
        total.addComponent(mainLayout);

    }

    private void initBlockImages() {
        HiLog.debug(LABEL_LOG, "执行：initBlockImages()");
        secondLayout.setColumnCount(this.picLen);
        secondLayout.setRowCount(this.picLen);
        secondLayout.removeAllComponents();
        imagesBlockList.clear();
        int tableLayoutWidth = (int) (phone_width * 0.8);
        //信息
        int partWidth = (int) (tableLayoutWidth / this.picLen);
        int partMargin = (int) (partWidth * 0.01);
        for (int i = 0; i < this.picLen; i++) {
            for (int j = 0; j < this.picLen; j++) {
                Image part = new Image(this);
                part.setWidth(partWidth);
                part.setHeight(partWidth);
                part.setId(i * picLen + j);
                part.setScaleMode(Image.ScaleMode.STRETCH);
                part.setMarginsTopAndBottom(partMargin, partMargin);
                part.setMarginsLeftAndRight(partMargin, partMargin);
                this.imagesBlockList.add(part);
                secondLayout.addComponent(part);
            }
        }
        for (Image image : imagesBlockList) {
            image.setClickedListener(new Component.ClickedListener() {
                @Override
                public void onClick(Component component) {
                    ImageBlockClick(component);
                }
            });
        }
        HiLog.debug(LABEL_LOG, "----imgBlockVector.size() = %{public}d, imagesBlockList.size() = %{public}d", imgBlockVector.size(), imagesBlockList.size());
        // 展示图片
        for (int i = 0; i < imgBlockVector.size(); i++) {
            imagesBlockList.get(i).setPixelMap(imgBlockVector.get(i));
            imagesBlockList.get(i).setVisibility(Component.VISIBLE);
        }
    }

    // 改变图片
    private void changeImage() {

        String[] permissions = {"ohos.permission.READ_USER_STORAGE"};
        requestPermissionsFromUser(permissions, 0);
        selectPic();

    }

    //保存图片到相册 fileName文件名  PixelMap 图片数据
    private void saveImageToLibrary(String fileName, PixelMap pixelMap) {
        HiLog.debug(LABEL_LOG, "执行：saveImageToLibrary(): 选择图片");

        try {
            ValuesBucket valuesBucket = new ValuesBucket();
            //文件名
            valuesBucket.putString(AVStorage.Images.Media.DISPLAY_NAME, fileName);
            //相对路径
            valuesBucket.putString("relative_path", "DCIM/");
            //文件格式，类型要一定要注意要是JPEG，PNG类型不支持
            valuesBucket.putString(AVStorage.Images.Media.MIME_TYPE, "image/JPEG");
            //应用独占：is_pending设置为1时表示只有该应用能访问此图片，其他应用无法发现该图片，当图片处理操作完成后再吧is_pending设置为0，解除独占，让其他应用可见
            valuesBucket.putInteger("is_pending", 1);

            //鸿蒙的helper.insert方法和安卓的contentResolver.insert方法有所不同，安卓方法直接返回一个uri，我们就可以拿来直接操作，而鸿蒙方法返回官方描述是Returns the index of the inserted data record（返回插入的数据记录的索引），这个index我的理解就是id，因此，我们需要自己在后面拼出文件的uri再进行操作
            DataAbilityHelper helper = DataAbilityHelper.creator(this);
            int index = helper.insert(AVStorage.Images.Media.EXTERNAL_DATA_ABILITY_URI, valuesBucket);
            Uri uri = Uri.appendEncodedPathToUri(AVStorage.Images.Media.EXTERNAL_DATA_ABILITY_URI, String.valueOf(index));

            //获取到uri后，安卓通过contentResolver.openOutputStream(uri)就能获取到输出流来写文件，而鸿蒙没有提供这样的方法，我们就只能通过uri获取FileDescriptor，再通过FileDescriptor生成输出流打包编码成新的图片文件，这里helper.openFile方法一定要有“w”写模式，不然会报FileNotFound的错误。
            FileDescriptor fd = helper.openFile(uri, "w");
            ImagePacker imagePacker = ImagePacker.create();
            ImagePacker.PackingOptions packingOptions = new ImagePacker.PackingOptions();
            OutputStream outputStream = new FileOutputStream(fd);
            packingOptions.format = "image/jpeg";
            packingOptions.quality = 90;
            boolean result = imagePacker.initializePacking(outputStream, packingOptions);
            if (result) {
                result = imagePacker.addImage(pixelMap);
                if (result) {
                    long dataSize = imagePacker.finalizePacking();
                }
            }
            outputStream.flush();
            outputStream.close();
            valuesBucket.clear();
            //解除独占
            valuesBucket.putInteger("is_pending", 0);
            helper.update(uri, valuesBucket, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 打开相册
    private void selectPic() {
        HiLog.debug(LABEL_LOG, "执行selectPic(): 选择图片");
        Intent intent = new Intent();
        Operation opt = new Intent.OperationBuilder().withAction("android.intent.action.GET_CONTENT").build();
        intent.setOperation(opt);
        intent.addFlags(Intent.FLAG_NOT_OHOS_COMPONENT);
        intent.setType("image/*");

        startAbilityForResult(intent, imgRequestCode);
    }

    // 获取选中的图片
    @Override
    protected void onAbilityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == imgRequestCode) {
            HiLog.info(LABEL_LOG, "选择图片getUriString:" + resultData.getUriString());
            //选择的Img对应的Uri
            String chooseImgUri = resultData.getUriString();
            HiLog.info(LABEL_LOG, "选择图片getScheme:" + chooseImgUri.substring(chooseImgUri.lastIndexOf('/')));
            //定义数据能力帮助对象
            DataAbilityHelper helper = DataAbilityHelper.creator(getContext());
            //定义图片来源对象
            ImageSource imageSource = null;
            //获取选择的Img对应的Id
            String chooseImgId;
            //如果是选择文件则getUriString结果为content://com.android.providers.media.documents/document/image%3A30，其中%3A是":"的URL编码结果，后面的数字就是image对应的Id
            //如果选择的是图库则getUriString结果为content://media/external/images/media/30，最后就是image对应的Id
            //这里需要判断是选择了文件还是图库
            if (chooseImgUri.lastIndexOf("%3A") != -1) {
                chooseImgId = chooseImgUri.substring(chooseImgUri.lastIndexOf("%3A") + 3);
            } else {
                chooseImgId = chooseImgUri.substring(chooseImgUri.lastIndexOf('/') + 1);
            }
            //获取图片对应的uri，由于获取到的前缀是content，我们替换成对应的dataability前缀
            Uri uri = Uri.appendEncodedPathToUri(AVStorage.Images.Media.EXTERNAL_DATA_ABILITY_URI, chooseImgId);
            HiLog.info(LABEL_LOG, "选择图片dataability路径:" + uri.toString());
            try {
                //读取图片
                FileDescriptor fd = helper.openFile(uri, "r");
                imageSource = ImageSource.create(fd, null);
                //创建位图
                PixelMap pixelMap = imageSource.createPixelmap(null);
                //设置图片控件对应的位图
                present_image.setPixelMap(pixelMap);
                isPicChange = true;
                initParams();
                initImages();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (imageSource != null) {
                    imageSource.release();
                }
            }
        }
    }

    private void initParams() {
        HiLog.debug(LABEL_LOG, "执行：initParams()");
        isRunning = false;
        resetTime();
        resetStep();
        for (Image image : imagesBlockList) {
            image.setVisibility(Component.VISIBLE);
        }
        blank_pos_row = blank_pos_col = picLen * picLen - 1;
    }


    // 开始游戏按钮
    private void StartGame() {
        HiLog.debug(LABEL_LOG, "执行：StartGame()");
        // 初始化可见文字参数
        initParams();
        // 将图片的id与块绑定
        // 更新imgBlockVector的图片列表
//        if (isChangeSize) {
        initImages();
//            isChangeSize = false;
//        }
        // 打乱图片
        shuffleImageBlocks();
        isRunning = true;
        startTime();
        HiLog.debug(LABEL_LOG, "开始游戏, imagesBlockSize = %{public}d, 空白块id = %{public}d", imagesBlockList.size(), blank_pos);
    }

    private void ChangeModel(Object tag) {
        switch (tag.toString()) {
            case "2x2":
                picLen = 2;
                break;
            case "3x3":
                picLen = 3;
                break;
            case "4x4":
                picLen = 4;
                break;
            case "5x5":
                picLen = 5;
                break;
        }
        isChangeSize = true;
        HiLog.debug(LABEL_LOG, "执行： ChangeModel(), 改变难度：picLen = %{public}d", picLen);
    }


    private void ImageBlockClick(Component component) {
        HiLog.debug(LABEL_LOG, "执行： ImageBlockClick()");
        // 相当于创建线程锁，如果在没有移动完，那么点击图片不会触发移动操作。
        if (!isMoving && isRunning) {
            isMoving = true;
            int tableLayoutWidth = (int) (phone_width * 0.8);
            int partWidth = tableLayoutWidth / picLen;
            int partMargin = (int) (partWidth * 0.01);

            //当前点击的Image相对位置
            click_pos = component.getId();
            for (int i = 0; i < imgBlockVector.size(); i++) {
                if (imagesBlockList.get(i).getId() == click_pos) {
                    click_index_in_list = i;
                }
                if (imagesBlockList.get(i).getId() == blank_pos) {
                    blank_index_in_list = i;
                }
            }
            HiLog.debug(LABEL_LOG, "click_index_in_list = %{public}d, blank_index_in_list = %{public}d,", click_index_in_list, blank_index_in_list);
            Image clickImage = imagesBlockList.get(click_index_in_list);
            Image blankImage = imagesBlockList.get(blank_index_in_list);
            int click_pos_row = click_pos / picLen;
            // 记录空白块的位置
            int click_pos_col = click_pos % picLen;

            //空白块的位置
            blank_pos_row = blank_pos / picLen;
            blank_pos_col = blank_pos % picLen;

            //确认相邻,相邻则交换背景
            if (Math.abs(click_pos_row - blank_pos_row) + Math.abs(click_pos_col - blank_pos_col) == 1) {
                // 交换两个图片在数组中的位置
                blankImage.setId(click_pos);
                clickImage.setId(blank_pos);
                Collections.swap(imagesBlockList, click_index_in_list, blank_index_in_list);

//            ///////////////////////////////////////////////////////////////////////

                AnimatorProperty animatorProperty = new AnimatorProperty();
                animatorProperty.setTarget(clickImage);
                AnimatorProperty animatorProperty2 = new AnimatorProperty();
                animatorProperty2.setTarget(blankImage);

                AnimatorGroup animatorGroup = new AnimatorGroup();
                HiLog.debug(LABEL_LOG, "click_pos = %{public}d, blank_pos = %{public}d, ", click_pos, blank_pos);

                animatorProperty.moveFromX(click_pos_col * (partWidth + partMargin * 2)).moveToX(blank_pos_col * (partWidth + partMargin * 2));
                animatorProperty.moveFromY(click_pos_row * (partWidth + partMargin * 2)).moveToY(blank_pos_row * (partWidth + partMargin * 2));
                animatorProperty2.moveFromX(blank_pos_col * (partWidth + partMargin * 2)).moveToX(click_pos_col * (partWidth + partMargin * 2));
                animatorProperty2.moveFromY(blank_pos_row * (partWidth + partMargin * 2)).moveToY(click_pos_row * (partWidth + partMargin * 2));
                animatorProperty.setDuration(50);
                animatorProperty2.setDuration(50);
                animatorGroup.runParallel(animatorProperty, animatorProperty2);
                animatorGroup.start();
//            //////////////////////////////////////////////////////////////////////////
                blank_pos = click_pos;
                HiLog.debug(LABEL_LOG, "----imagesBlockList  ID: %{public}d, %{public}d, %{public}d, %{public}d", imagesBlockList.get(0).getId(), imagesBlockList.get(1).getId(), imagesBlockList.get(2).getId(), imagesBlockList.get(3).getId());
                HiLog.debug(LABEL_LOG, "----imagesBlockList TAG: %{public}d, %{public}d, %{public}d, %{public}d", imagesBlockList.get(0).getTag(), imagesBlockList.get(1).getTag(), imagesBlockList.get(2).getTag(), imagesBlockList.get(3).getTag());
                HiLog.debug(LABEL_LOG, "----点击图块:点击后：pic_TAG = %{public}s, pic_id = %{public}d, 空白位置: blank_pos = %{public}d", component.getTag(), component.getId(), blank_pos);
                clickCount += 1;
                countText_count.setText(String.valueOf(clickCount));
                checkComplete();
            }
            isMoving = false;
        }
    }

    private void checkComplete() {
        boolean complete = true;
        for (Image image : imagesBlockList) {
            if (image.getId() != (Integer) image.getTag()) {
                complete = false;
                break;
            }
        }
        if (complete) {
            isRunning = false;
            tickTimer.stop();
            showDialogSuccess();
        }
    }

    private void shuffleImageBlocks() {
        Vector<Integer> indexList = new Vector<>();
        for (int i = 0; i < imgBlockVector.size(); i++)
            indexList.add(i);

        // 更新图片
        // 生成有解的随机数列
        int n = 1;
        while (n % 2 == 1) {
            n = 0;
            Collections.shuffle(indexList);
            // 判断是否有解
            for (int i = 0; i < indexList.size() - 1; i++) {

                if (indexList.get(i) > indexList.get(i + 1))
                    n++;
            }
            if (n % 2 == 1) continue;
            imagesBlockList.clear();
            initBlockImages();
            for (int i = 0; i < imgBlockVector.size(); i++) {
                if (indexList.get(i) == picLen * picLen - 1) {
                    blank_pos = i;
                    imagesBlockList.get(blank_pos).setVisibility(Component.INVISIBLE);
                }
                imagesBlockList.get(i).setTag(indexList.get(i));
                imagesBlockList.get(i).setPixelMap(imgBlockVector.get(indexList.get(i)));
            }
        }

    }

    private void resetStep() {
        clickCount = 0;
        countText_count.setText("0");
    }


    //拼图成功对话框
    public void showDialogSuccess() {
        CommonDialog cd = new CommonDialog(this);
        cd.setTitleText("恭喜! 挑战成功");
        String successText = this.tickTimer.getText();
        cd.setContentText("  用时：" + successText);
        cd.setSize(800, MATCH_CONTENT);
        cd.setButton(1, "确认", new IDialog.ClickedListener() {
            @Override
            public void onClick(IDialog iDialog, int i) {
                cd.destroy();
            }
        });
        cd.setAutoClosable(true);
        cd.show();
    }


    //开始计时
    public void startTime() {
        this.startTime = 0;
        this.currentTime = System.currentTimeMillis();
        this.tickTimer.setBaseTime(currentTime - startTime);
        this.tickTimer.start();
    }

    //重置时间
    public void resetTime() {
        tickTimer.stop();
        this.tickTimer.setBaseTime(0);
        this.startTime = 0;
        this.currentTime = 0;
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }
}
