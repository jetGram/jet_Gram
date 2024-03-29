/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2016.
 */

package org.telegram.ui.Cells;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.finalsoft.messenger.R;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.CheckBoxSquare;
import org.telegram.ui.Components.LayoutHelper;

public class UserCell extends FrameLayout {

    private BackupImageView avatarImageView;
    private SimpleTextView nameTextView;
    private SimpleTextView statusTextView;
    private ImageView imageView;
    private CheckBox checkBox;
    private CheckBoxSquare checkBoxBig;
    private ImageView adminImage;

    private ImageView mutualImageView;

    private AvatarDrawable avatarDrawable;
    private TLObject currentObject = null;

    private CharSequence currentName;
    private CharSequence currrntStatus;
    private int currentDrawable;

    private String lastName = null;
    private int lastStatus = 0;
    private TLRPC.FileLocation lastAvatar = null;

    private int statusColor = 0xffa8a8a8;
    private int statusOnlineColor = AndroidUtilities.getIntDarkerColor("themeColor", 0x15);//0xff3b84c0;

    private int nameColor = 0xff000000;

    private Drawable curDrawable = null;

    private int radius = 32;

    public UserCell(Context context, int padding, int checkbox, boolean admin) {
        super(context);

        avatarDrawable = new AvatarDrawable();

        avatarImageView = new BackupImageView(context);
        avatarImageView.setRoundRadius(AndroidUtilities.dp(24));
        addView(avatarImageView, LayoutHelper.createFrame(48, 48, (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, LocaleController.isRTL ? 0 : 7 + padding, 8, LocaleController.isRTL ? 7 + padding : 0, 0));

        nameTextView = new SimpleTextView(context);
        nameTextView.setTextColor(0xff212121);
        nameTextView.setTextSize(17);
        nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        nameTextView.setGravity((LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP);
        addView(nameTextView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 20, (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, LocaleController.isRTL ? 28 + (checkbox == 2 ? 18 : 0) : (68 + padding), 11.5f, LocaleController.isRTL ? (68 + padding) : 28 + (checkbox == 2 ? 18 : 0), 0));

        mutualImageView = new ImageView(context);
        mutualImageView.setScaleType(ImageView.ScaleType.CENTER);
        mutualImageView.setVisibility(GONE);
        mutualImageView.setImageResource(R.drawable.ic_contacts_mutual);
        addView(mutualImageView, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 3 : 5) | 48, LocaleController.isRTL ? 7.0f : 0.0f, 8.0f, LocaleController.isRTL ? 0.0f : 7.0f, 0.0f));



        statusTextView = new SimpleTextView(context);
        statusTextView.setTextSize(14);
        statusTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        statusTextView.setGravity((LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP);
        addView(statusTextView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 20, (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, LocaleController.isRTL ? 28 : (68 + padding), 34.5f, LocaleController.isRTL ? (68 + padding) : 28, 0));

        imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setVisibility(GONE);
        addView(imageView, LayoutHelper.createFrame(LayoutParams.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.CENTER_VERTICAL, LocaleController.isRTL ? 0 : 16, 0, LocaleController.isRTL ? 16 : 0, 0));


        if (checkbox == 2) {
            checkBoxBig = new CheckBoxSquare(context);
            addView(checkBoxBig, LayoutHelper.createFrame(18, 18, (LocaleController.isRTL ? Gravity.LEFT : Gravity.RIGHT) | Gravity.CENTER_VERTICAL, LocaleController.isRTL ? 19 : 0, 0, LocaleController.isRTL ? 0 : 19, 0));
        } else if (checkbox == 1) {
            checkBox = new CheckBox(context, R.drawable.round_check2);
            checkBox.setVisibility(INVISIBLE);
            addView(checkBox, LayoutHelper.createFrame(22, 22, (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, LocaleController.isRTL ? 0 : 37 + padding, 38, LocaleController.isRTL ? 37 + padding : 0, 0));
        }

        if (admin) {
            adminImage = new ImageView(context);
            adminImage.setImageResource(R.drawable.admin_star);
            addView(adminImage, LayoutHelper.createFrame(16, 16, (LocaleController.isRTL ? Gravity.LEFT : Gravity.RIGHT) | Gravity.TOP, LocaleController.isRTL ? 24 : 0, 13.5f, LocaleController.isRTL ? 0 : 24, 0));
        }
    }

    public void setIsAdmin(int value) {
        if (adminImage == null) {
            return;
        }
        adminImage.setVisibility(value != 0 ? VISIBLE : GONE);
        nameTextView.setPadding(LocaleController.isRTL && value != 0 ? AndroidUtilities.dp(16) : 0, 0, !LocaleController.isRTL && value != 0 ? AndroidUtilities.dp(16) : 0, 0);
        if (value == 1) {
            adminImage.setImageResource(R.drawable.admin_star);
        } else if (value == 2) {
            adminImage.setImageResource(R.drawable.admin_star2);
        }
    }

    public void setData(TLObject user, CharSequence name, CharSequence status, int resId) {
        if (user == null) {
            currrntStatus = null;
            currentName = null;
            currentObject = null;
            statusTextView.setText("");
            avatarImageView.setImageDrawable(null);
            if (name == null)
                nameTextView.setText("");
            else {
                String spaceStr = LocaleController.isRTL ?"  ":"";

                nameTextView.setText(spaceStr+String.valueOf(name.toString()));
                update(0);
            }


            return;
        }

        currrntStatus = status;
        currentName = name;
        currentObject = user;
        currentDrawable = resId;
        update(0);
    }

    private void updateTheme() {
        SharedPreferences themePrefs = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, AndroidUtilities.THEME_PREFS_MODE);
        String tag = getTag() != null ? getTag().toString() : "";
        if (tag.contains("Contacts")) {
            setStatusColors(themePrefs.getInt("contactsStatusColor", 0xffa8a8a8), themePrefs.getInt("contactsOnlineColor", AndroidUtilities.getIntDarkerColor("themeColor", 0x15)));
            nameColor = themePrefs.getInt("contactsNameColor", 0xff212121);
            nameTextView.setTextColor(nameColor);
            nameTextView.setTextSize(themePrefs.getInt("contactsNameSize", 17));
            setStatusSize(themePrefs.getInt("contactsStatusSize", 14));
            setAvatarRadius(themePrefs.getInt("contactsAvatarRadius", 32));
        } else if (tag.contains("Profile")) {
            setStatusColors(themePrefs.getInt("profileSummaryColor", 0xff8a8a8a), themePrefs.getInt("profileOnlineColor", AndroidUtilities.getIntDarkerColor("themeColor", -0x40)));
            nameColor = themePrefs.getInt("profileTitleColor", 0xff212121);
            nameTextView.setTextColor(nameColor);
            nameTextView.setTextSize(17);
            setStatusSize(14);
            //setAvatarRadius(32);
            setAvatarRadius(themePrefs.getInt("profileRowAvatarRadius", 32));
            int dColor = themePrefs.getInt("profileIconsColor", 0xff737373);
            if (currentDrawable != 0) {
                Drawable d = getResources().getDrawable(currentDrawable);
                d.setColorFilter(dColor, PorterDuff.Mode.SRC_IN);
            }
            if (adminImage != null) adminImage.setColorFilter(dColor, PorterDuff.Mode.SRC_IN);
        } else if (tag.contains("Pref")) {
            setStatusColors(themePrefs.getInt("prefSummaryColor", 0xff8a8a8a), AndroidUtilities.getIntDarkerColor("themeColor", -0x40));
            nameColor = themePrefs.getInt("prefTitleColor", 0xff212121);
            nameTextView.setTextColor(nameColor);
        }
    }

    public void setChecked(boolean checked, boolean animated) {
        if (checkBox != null) {
            if (checkBox.getVisibility() != VISIBLE) {
                checkBox.setVisibility(VISIBLE);
            }
            checkBox.setChecked(checked, animated);
        } else if (checkBoxBig != null) {
            if (checkBoxBig.getVisibility() != VISIBLE) {
                checkBoxBig.setVisibility(VISIBLE);
            }
            checkBoxBig.setChecked(checked, animated);
        }
    }

    public void setCheckDisabled(boolean disabled) {
        if (checkBoxBig != null) {
            checkBoxBig.setDisabled(disabled);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64), MeasureSpec.EXACTLY));
    }

    public void setStatusColors(int color, int onlineColor) {
        statusColor = color;
        statusOnlineColor = onlineColor;
    }

    public void update(int mask) {
        if (currentObject == null) {
            return;
        }
        TLRPC.FileLocation photo = null;
        String newName = null;
        TLRPC.User currentUser = null;
        TLRPC.Chat currentChat = null;
        if (currentObject instanceof TLRPC.User) {
            currentUser = (TLRPC.User) currentObject;
            if (currentUser.photo != null) {
                photo = currentUser.photo.photo_small;
            }
        } else {
            currentChat = (TLRPC.Chat) currentObject;
            if (currentChat.photo != null) {
                photo = currentChat.photo.photo_small;
            }
        }
        updateTheme();
        if (mask != 0) {
            boolean continueUpdate = false;
            if ((mask & MessagesController.UPDATE_MASK_AVATAR) != 0) {
                if (lastAvatar != null && photo == null || lastAvatar == null && photo != null && lastAvatar != null && photo != null && (lastAvatar.volume_id != photo.volume_id || lastAvatar.local_id != photo.local_id)) {
                    continueUpdate = true;
                }
            }
            if (currentUser != null && !continueUpdate && (mask & MessagesController.UPDATE_MASK_STATUS) != 0) {
                int newStatus = 0;
                if (currentUser.status != null) {
                    newStatus = currentUser.status.expires;
                }
                if (newStatus != lastStatus) {
                    continueUpdate = true;
                }
            }
            if (!continueUpdate && currentName == null && lastName != null && (mask & MessagesController.UPDATE_MASK_NAME) != 0) {
                if (currentUser != null) {
                    newName = UserObject.getUserName(currentUser);
                } else {
                    newName = currentChat.title;
                }
                if (!newName.equals(lastName)) {
                    continueUpdate = true;
                }
            }
            if (!continueUpdate) {
                return;
            }
        }

        if (currentUser != null) {
            avatarDrawable.setInfo(currentUser);
            if (currentUser.status != null) {
                lastStatus = currentUser.status.expires;
            } else {
                lastStatus = 0;
            }
        } else {
            avatarDrawable.setInfo(currentChat);
        }

        String spaceStr = LocaleController.isRTL ?"  ":"";

        if (currentName != null) {
            lastName = null;
            nameTextView.setText(spaceStr+currentName);
        } else {
            if (currentUser != null) {
                lastName = newName == null ? UserObject.getUserName(currentUser) : newName;
            } else {
                lastName = newName == null ? currentChat.title : newName;
            }
            nameTextView.setText(spaceStr+lastName);
        }
        if (currrntStatus != null) {
            statusTextView.setTextColor(statusColor);
            statusTextView.setText(spaceStr+currrntStatus);
        } else if (currentUser != null) {
            if (currentUser.bot) {
                statusTextView.setTextColor(statusColor);
                if (currentUser.bot_chat_history) {
                    statusTextView.setText(spaceStr+LocaleController.getString("BotStatusRead", R.string.BotStatusRead));
                } else {
                    statusTextView.setText(spaceStr+LocaleController.getString("BotStatusCantRead", R.string.BotStatusCantRead));
                }
            } else {
                if (currentUser.id == UserConfig.getClientUserId() || currentUser.status != null && currentUser.status.expires > ConnectionsManager.getInstance().getCurrentTime() || MessagesController.getInstance().onlinePrivacy.containsKey(currentUser.id)) {
                    statusTextView.setTextColor(statusOnlineColor);
                    statusTextView.setText(spaceStr+LocaleController.getString("Online", R.string.Online));
                } else {
                    statusTextView.setTextColor(statusColor);
                    statusTextView.setText(spaceStr+LocaleController.formatUserStatus(currentUser));
                }
            }
        }

        if (imageView.getVisibility() == VISIBLE && currentDrawable == 0 || imageView.getVisibility() == GONE && currentDrawable != 0) {
            imageView.setVisibility(currentDrawable == 0 ? GONE : VISIBLE);
            imageView.setImageResource(currentDrawable);
            if (currentDrawable != 0)
                imageView.setImageDrawable(getResources().getDrawable(currentDrawable));
        }
        //Customized:
        if (curDrawable != null) imageView.setImageDrawable(curDrawable);
        avatarImageView.getImageReceiver().setRoundRadius(AndroidUtilities.dp(radius));
        avatarDrawable.setRadius(AndroidUtilities.dp(radius));

        avatarImageView.setImage(photo, "50_50", avatarDrawable);

        TLRPC.User user = null;
        if (currentObject != null) {
            if (currentObject instanceof TLRPC.User) {
                user = (TLRPC.User) currentObject;
            }
        }

        if (user != null && user.mutual_contact ) {
            mutualImageView.setVisibility(VISIBLE);
        } else {
            mutualImageView.setVisibility(GONE);
        }

        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
        boolean mode = sharedPreferences.getBoolean("ghost_mode", false);
        if (user != null && user.id == UserConfig.getCurrentUser().id && mode) {
            this.statusTextView.setTextColor(0xffa8a8a8);
            this.statusTextView.setText(spaceStr+LocaleController.getString("Hidden", R.string.Hidden));
        }


    }

    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }

    public void setNameColor(int color) {
        nameColor = color;
    }

    public void setNameSize(int size) {
        nameTextView.setTextSize(size);
    }

    public void setStatusColor(int color) {
        statusColor = color;
    }

    public void setStatusSize(int size) {
        statusTextView.setTextSize(size);
    }

    public void setImageDrawable(Drawable drawable) {
        curDrawable = drawable;
    }

    public void setAvatarRadius(int value) {
        radius = value;
    }
}
