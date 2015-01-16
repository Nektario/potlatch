package com.mocca_capstone.potlatch.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;
import com.mocca_capstone.potlatch.R;
import com.mocca_capstone.potlatch.effects.BaseEffects;
import com.mocca_capstone.potlatch.effects.Levels;
import com.mocca_capstone.potlatch.events.EventAddGift;
import com.mocca_capstone.potlatch.imageloader.ImageLoader;
import com.mocca_capstone.potlatch.models.Gift;
import com.mocca_capstone.potlatch.network.ApiTaskService;
import com.mocca_capstone.potlatch.network.UploadNewGiftCommand;
import com.mocca_capstone.potlatch.providers.EventBus;
import com.mocca_capstone.potlatch.providers.UserAccounts;
import com.mocca_capstone.potlatch.widgets.ProgressBarCircularIndeterminate;
import com.mocca_capstone.potlatch.widgets.StartMiddleSeekBar;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import static com.mocca_capstone.potlatch.utilities.MyLog.LOGD;

/**
 * Created by nektario on 11/4/2014.
 */
public class AddGiftActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "AddGiftActivity";
    private static final String EXTRA_PICTURE_SOURCE = "extra-picture-source";
    private static final String EXTRA_GIFT_PARENT_ID = "extra-gift-parent-id";
    private static final String STATE_PHOTO_PATH = "state-photo-path";
    private static final String STATE_IS_UPLOADING = "state-is-uploading";
    private static final String STATE_IS_UPLOADED = "state-is-uploaded";
    private static final String STATE_IS_ERRORED = "state-is-errored";
    private static final String PICTURE_DIR = "Potlatch";
    private static final File STORAGE_DIR =
                                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    private static final int TAKE_PICTURE_WITH_CAMERA_CODE = 10;
    private static final int PICK_PICTURE_FROM_GALLERY_CODE = 11;
    private static final int MINIMUM_TITLE_LENGTH = 2;
    public static final int USE_CAMERA = 1;
    public static final int USE_GALLERY = 2;
    private String mCurrentPhotoPath;
    private ViewGroup mEditGroup;
    private ViewGroup mResultGroup;
    private ImageView mPicture;
    private EditText mTitle;
    private EditText mDescription;
    private FloatingActionButton mUploadButton;
    private ProgressBarCircularIndeterminate mProgressBar;
    private boolean mIsUploading;
    private boolean mIsUploaded;
    private boolean mIsErrored;
    private long mParentGiftId;
    @Inject EventBus mEventBus;
    @Inject ImageLoader mImageLoader;
    @Inject UserAccounts mUserAccounts;


    // Image Effects
    private BaseEffects mEffect;
    public RenderScript mRS;
    public Allocation mInPixelsAllocation;
    public Allocation mOutPixelsAllocation;
    public Bitmap mAddedEffectBitmap;
    private StartMiddleSeekBar mContrastBar;
    private StartMiddleSeekBar mSaturationBar;
    private StartMiddleSeekBar mBrightnessBar;
    private int mRunCount;
    private boolean mAddedEffects = false;



    public static Intent newInstance(Context ctx, int pictureSource, long parentGiftId) {
        Intent intent = new Intent(ctx, AddGiftActivity.class);
        LOGD(TAG, "Parent Id: " + parentGiftId);
        intent.putExtra(EXTRA_PICTURE_SOURCE, pictureSource);
        intent.putExtra(EXTRA_GIFT_PARENT_ID, parentGiftId);

        return intent;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mCurrentPhotoPath != null) {
            outState.putString(STATE_PHOTO_PATH, mCurrentPhotoPath);
            outState.putBoolean(STATE_IS_UPLOADED, mIsUploaded);
            outState.putBoolean(STATE_IS_UPLOADING, mIsUploading);
            outState.putBoolean(STATE_IS_ERRORED, mIsErrored);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_gift);

        mEventBus.register(this);
        initializeActionBarToolbar(false, true);
        getActionBarToolbar().setBackgroundColor(getResources().getColor(android.R.color.transparent));
        getActionBarToolbar().setNavigationIcon(R.drawable.ic_ab_back_white);

        mEditGroup = (ViewGroup) findViewById(R.id.add_gift_edit_group);
        mResultGroup = (ViewGroup) findViewById(R.id.add_gift_result_group);
        mProgressBar = (ProgressBarCircularIndeterminate) findViewById(R.id.add_gift_progressbar);
        mPicture = (ImageView) findViewById(R.id.add_gift_picture);
        mUploadButton = (FloatingActionButton) findViewById(R.id.add_gift_upload_fab);
        mDescription = (EditText) findViewById(R.id.add_gift_description);
        mTitle = (EditText) findViewById(R.id.add_gift_title);
        mTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                if (mTitle.getText().length() >= MINIMUM_TITLE_LENGTH) {
                    if (!isUploadButtonEnabled()) {
                        enableUploadButton();
                    }
                } else {
                    disableUploadButton();
                }
            }
        });

        mImageLoader.load(mUserAccounts.getActiveUser().getProfilePhotoUrl(),
                                        ((ImageView) findViewById(R.id.add_gift_result_profile_pic)));

        if (savedInstanceState != null) {
            mCurrentPhotoPath = savedInstanceState.getString(STATE_PHOTO_PATH);
            mIsUploading = savedInstanceState.getBoolean(STATE_IS_UPLOADING);
            mIsUploaded = savedInstanceState.getBoolean(STATE_IS_UPLOADED);
            mIsErrored = savedInstanceState.getBoolean(STATE_IS_ERRORED);
            displayPictureThumbnail();
        } else {
            int pictureSource = getIntent().getIntExtra(EXTRA_PICTURE_SOURCE, 0);
            mParentGiftId = getIntent().getLongExtra(EXTRA_GIFT_PARENT_ID, Gift.CHAIN_ROOT_PARENT_ID);
            switch (pictureSource) {
                case USE_CAMERA:
                    dispatchTakePictureWithCameraIntent();
                    break;
                case USE_GALLERY:
                    dispatchPickPictureFromGalleryIntent();
                    break;
            }
        }
    }

    private void dispatchTakePictureWithCameraIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, TAKE_PICTURE_WITH_CAMERA_CODE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_" + System.nanoTime() + ".jpg";
        File image = new File(getPictureDir() + imageFileName);
        LOGD(TAG, "Save picture to: " + image.getAbsolutePath());

        mCurrentPhotoPath =  image.getAbsolutePath();
        return image;
    }

    private File getPictureDir() {
        makePictureDirIfNotExists();
        return new File(STORAGE_DIR + File.separator + PICTURE_DIR + File.separator);
    }

    private void makePictureDirIfNotExists() {
        File pictureDir = new File(STORAGE_DIR + File.separator + PICTURE_DIR);
        if (!pictureDir.exists()) {
            pictureDir.mkdir();
        }
    }

    private void dispatchPickPictureFromGalleryIntent() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        if (photoPickerIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(photoPickerIntent, PICK_PICTURE_FROM_GALLERY_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_PICTURE_WITH_CAMERA_CODE && resultCode == Activity.RESULT_OK) {
            displayPictureThumbnail();
            addPhotoToMediaStore();
        } else if (requestCode == PICK_PICTURE_FROM_GALLERY_CODE && resultCode == Activity.RESULT_OK) {
            getPicturePathFromMediaStore(data.getData());
            displayPictureThumbnail();
        } else {
            finish();
        }
        showFab();
        showEditFields();
    }

    private void showFab() {
        findViewById(R.id.add_gift_upload_fab).setVisibility(View.VISIBLE);
    }

    private void showEditFields() {
        mEditGroup.setVisibility(View.VISIBLE);
    }
    private void hideEditFields() {
        mEditGroup.setVisibility(View.GONE);
    }

    private void showResultFields() {
        ((TextView) findViewById(R.id.add_gift_result_title)).setText(mTitle.getText().toString());
        if (mDescription.getText().length() > 0) {
            ((TextView) mResultGroup.findViewById(R.id.add_gift_result_description))
                                                                .setText(mDescription.getText().toString());
            mResultGroup.findViewById(R.id.add_gift_result_description).setVisibility(View.VISIBLE);
        }
        mResultGroup.setVisibility(View.VISIBLE);
    }

    private void getPicturePathFromMediaStore(Uri uri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        mCurrentPhotoPath = cursor.getString(columnIndex);
        cursor.close();
    }

    private void displayPictureThumbnail() {
        // Get the dimensions of the View
        int targetW = 1920;
        int targetH = 1920;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        //mPicture.setImageBitmap(bitmap);
        initEffects(bitmap);
    }


    /*
     * Upload button
     */
    private void setUploadState() {
        if (mIsUploaded) {
            setStateUploaded();
        } else if (mIsUploading) {
            setStateUploading();
        } else {
            setStateReadyToUpload();
        }
    }

    private void setStateUploading() {
        mIsUploading = true;
        mIsUploaded = false;
        mUploadButton.setOnClickListener(null);
        showProgressBar();
    }

    private void setStateUploaded() {
        mIsUploading = false;
        mIsUploaded = true;
        mUploadButton.setOnClickListener(null);
        mUploadButton.setEnabled(false);
        hideProgressBar();
        displaySuccessDrawable();
        hideEditFields();
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.uploaded_gift_card);
        mResultGroup.startAnimation(animation);
        showResultFields();
    }

    private void setStateReadyToUpload() {
        mIsUploading = false;
        mIsUploaded = false;
        enableOrDisableUploadButton();
        hideProgressBar();
        mUploadButton.setImageResource(R.drawable.ic_action_upload_gift);
        mUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mIsUploading) {
                    uploadNewGift();
                }
            }
        });
    }

    private void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        mProgressBar.setVisibility(View.GONE);
    }

    private void displaySuccessDrawable() {
        mUploadButton.setImageResource(R.drawable.ic_success_checkmark);
        mUploadButton.setColorNormalResId(R.color.color__white);
    }

    private void addPhotoToMediaStore() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void uploadNewGift() {
        setStateUploading();
        if (mAddedEffects) {
            LOGD(TAG, "We Added effects");
            new SaveEditedPictureAndUploadTask().execute();
        } else {
            startGiftUpload();
        }
    }

    private void startGiftUpload() {
        Gift gift = new Gift(mParentGiftId, mTitle.getText().toString(), mDescription.getText().toString());
        startService(ApiTaskService.newInstance(getApplicationContext(),
                                                new UploadNewGiftCommand(gift, mCurrentPhotoPath)));
    }

    private class SaveEditedPictureAndUploadTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            FileOutputStream out = null;
            try {
                createImageFile();
                out = new FileOutputStream(mCurrentPhotoPath);
                mAddedEffectBitmap.compress(Bitmap.CompressFormat.JPEG, 95, out);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            startGiftUpload();
        }
    }





    @Override
    public void onResume() {
        super.onResume();
        setUploadState();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mEventBus.unRegister(this);
        cleanup();

    }

    private void enableOrDisableUploadButton() {
        if (mTitle.getText().length() >= MINIMUM_TITLE_LENGTH) {
            enableUploadButton();
        } else {
            disableUploadButton();
        }
    }

    private void enableUploadButton() {
        mUploadButton.setEnabled(true);
        mUploadButton.setColorNormal(getResources().getColor(R.color.theme_accent_1));
    }

    private void disableUploadButton() {
        mUploadButton.setEnabled(false);
        mUploadButton.setColorNormal(getResources().getColor(R.color.disabled_upload_button));
    }

    private boolean isUploadButtonEnabled() {
        return mUploadButton.isEnabled();
    }

    @Subscribe
    public void handleAddGiftResult(EventAddGift event) {
        if (event.isSuccessStatusCode()) {
            LOGD(TAG, "Success");
            setStateUploaded();
        } else {
            LOGD(TAG, "Failure");
            Toast.makeText(getApplicationContext(), R.string.error_uploading_gift, Toast.LENGTH_SHORT).show();
            setStateReadyToUpload();
        }
    }



    /*
     *
     *  Image Effects
     *
     */
    public void updateDisplay() {
        mHandler.sendMessage(Message.obtain());
    }

    private Handler mHandler = new Handler() {
        // Allow the filter to complete without blocking the UI
        // thread.  When the message arrives that the operation is complete
        // we will either mark completion or start a new filter if
        // more work is ready.  Either way, display the result.
        @Override
        public void handleMessage(Message msg) {
            boolean doTest = false;
            synchronized(this) {
                if (mRS == null) {
                    return;
                }
                mEffect.updateBitmap(mAddedEffectBitmap);
                mPicture.invalidate();
                if (mRunCount > 0) {
                    mRunCount--;
                    if (mRunCount > 0) {
                        doTest = true;
                    }
                }

                if (doTest) {
                    mEffect.runEffectSendMessage();
                }
            }
        }
    };

    private void initEffects(Bitmap bitmap) {
        mRS = RenderScript.create(this);
        mInPixelsAllocation = Allocation.createFromBitmap(mRS, bitmap);
        mAddedEffectBitmap = Bitmap.createBitmap(mInPixelsAllocation.getType().getX(),
                mInPixelsAllocation.getType().getY(),
                Bitmap.Config.ARGB_8888);
        mAddedEffectBitmap.setHasAlpha(false);
        mOutPixelsAllocation = Allocation.createFromBitmap(mRS, mAddedEffectBitmap);
        mPicture.setImageBitmap(mAddedEffectBitmap);

        mContrastBar = (StartMiddleSeekBar) findViewById(R.id.add_gift_contrast_bar);
        mSaturationBar = (StartMiddleSeekBar) findViewById(R.id.add_gift_saturation_bar);
        mBrightnessBar = (StartMiddleSeekBar) findViewById(R.id.add_gift_brightness_bar);

        mContrastBar.setOnSeekBarChangeListener(this);
        mSaturationBar.setOnSeekBarChangeListener(this);
        mBrightnessBar.setOnSeekBarChangeListener(this);

        createEffect();
    }

    private void createEffect() {
        if (mEffect != null) {
            mEffect.destroy();
        }

        mEffect = new Levels(true, true);
        mEffect.createBaseEffect(this);
        mEffect.runEffect();
        updateDisplay();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            if (seekBar == mContrastBar) {
                mEffect.onContrastBarChanged(progress);
                mAddedEffects = true;
            } else if (seekBar == mSaturationBar) {
                mEffect.onSaturationBarChanged(progress);
                mAddedEffects = true;
            } else if (seekBar == mBrightnessBar) {
                mEffect.onBrightnessBarChanged(progress);
                mAddedEffects = true;
            }

            boolean doEffect = false;
            synchronized(this) {
                if (mRunCount == 0) {
                    doEffect = true;
                    mRunCount = 1;
                } else {
                    mRunCount = 2;
                }
            }
            if (doEffect) {
                mEffect.runEffectSendMessage();
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    void cleanup() {
        synchronized(this) {
            if (mRS != null) {
                RenderScript rs = mRS;
                mRS = null;
                rs.destroy();
            }
        }

        mInPixelsAllocation = null;
        mOutPixelsAllocation = null;
        mAddedEffectBitmap = null;
    }
}
