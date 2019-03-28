package com.example.datvtd.chatting.Animation;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper.Callback;
import android.view.MotionEvent;
import android.view.View;

import com.example.datvtd.chatting.InfoGroupActivity;
import com.example.datvtd.chatting.MessageActivity;
import com.example.datvtd.chatting.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.lang.reflect.Array;

import static android.support.v7.widget.helper.ItemTouchHelper.*;

enum ButtonsState {
    GONE,
    EDIT_VISIBLE_RIGHT,
    DELETE_VISIBLE_RIGHT,
    PROMOTE_VISIBLE_RIGHT
}

public class SwipeController extends Callback {
    public SwipeController(SwipeControllerActions buttonsActions) {
        this.buttonsActions = buttonsActions;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, LEFT); // chỉ muốn dùng một nút bên phải ( dùng cả 2 nút : LEFT | RIGHT
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        if (swipeBack) {
            swipeBack = buttonShowedState != ButtonsState.GONE;
            return 0;
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {

        user = InfoGroupActivity.mUsers.get(viewHolder.getAdapterPosition());
        adminGroup = MessageActivity.bundle.getString("adminGroup");

        // phân quyền xóa user khỏi group, nhượng admin
        if (firebaseUser.getUid().equals(adminGroup)) {
            if (actionState == ACTION_STATE_SWIPE) {
                if (buttonShowedState != ButtonsState.GONE) {
                    if (buttonShowedState == ButtonsState.EDIT_VISIBLE_RIGHT) {
                        dX = Math.max(dX, BUTTON_WIDTH);
                    }
                    if (buttonShowedState == ButtonsState.DELETE_VISIBLE_RIGHT) {
//                        dX = Math.min(dX, -BUTTON_WIDTH/2);
                        dX = Math.min(-BUTTON_WIDTH / 2, -BUTTON_WIDTH);
                    }
                    if (buttonShowedState == ButtonsState.PROMOTE_VISIBLE_RIGHT) {
                        dX = Math.min(-BUTTON_WIDTH / 2, -BUTTON_WIDTH);
                    }
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive); // set vị trí của nút cố định sau khi kéo
                } else {
                    setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }

            if (buttonShowedState == ButtonsState.GONE) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
            currentItemViewHolder = viewHolder;
        } else {
            if (firebaseUser.getUid().equals(user.getId())) {
                if (actionState == ACTION_STATE_SWIPE) {
                    if (buttonShowedState != ButtonsState.GONE) {
                        if (buttonShowedState == ButtonsState.EDIT_VISIBLE_RIGHT) {
                            dX = Math.max(dX, BUTTON_WIDTH);
                        }
                        if (buttonShowedState == ButtonsState.DELETE_VISIBLE_RIGHT) {
                            dX = Math.min(dX, -BUTTON_WIDTH / 2);
                        }
                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);  // set vị trí của nút cố định sau khi kéo
                    } else {
                        setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    }
                }

                if (buttonShowedState == ButtonsState.GONE) {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
                currentItemViewHolder = viewHolder;
            }
        }
    }

    private void setTouchListener(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder,
                                  final float dX, final float dY, final int actionState, final boolean isCurrentlyActive) {
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                swipeBack = event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP;
                if (swipeBack) {
                    //set kéo ra khoảng cách bao nhiêu thì nút hiện ra
                    if (dX > BUTTON_WIDTH / 2 && dX < BUTTON_WIDTH)
                        buttonShowedState = ButtonsState.EDIT_VISIBLE_RIGHT;
                    if (dX < -BUTTON_WIDTH / 2)
                        buttonShowedState = ButtonsState.PROMOTE_VISIBLE_RIGHT;
                    if (dX < -BUTTON_WIDTH / 2)
                        buttonShowedState = ButtonsState.DELETE_VISIBLE_RIGHT;
                    //  lưu ý giá trị buttonShowedState. Để như này có nghĩa là giá trị của nó đang là DELETE_VISIBLE_RIGHT

                    if (buttonShowedState != ButtonsState.GONE) {
                        setTouchDownListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                        setItemsClickable(recyclerView, false);
                    }
                }
                return false;
            }
        });
    }

    private void setTouchDownListener(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder,
                                      final float dX, final float dY, final int actionState, final boolean isCurrentlyActive) {
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    x = event.getX();
                    setTouchUpListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
                return false;
            }
        });
    }

    private void setTouchUpListener(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder,
                                    final float dX, final float dY, final int actionState, final boolean isCurrentlyActive) {
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    recyclerView.removeViewAt(viewHolder.getAdapterPosition()); // tránh tình trạng khi kéo xong nhả về sẽ xuất hiện 1 đường kẻ ở phía dưới mỗi item
                    SwipeController.super.onChildDraw(c, recyclerView, viewHolder, 0F, dY, actionState, isCurrentlyActive);
                    recyclerView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return false;
                        }
                    });
                    setItemsClickable(recyclerView, true);
                    swipeBack = false;

                    if (buttonsActions != null && buttonInstance[0] != null) {
                        if (buttonShowedState.equals(buttonShowedState.EDIT_VISIBLE_RIGHT)) {
                            if (buttonInstance[0].contains(x, event.getY())) {
                                buttonsActions.onEditButtonClicked(viewHolder.getAdapterPosition());
                            }
                        } else if (buttonShowedState.equals(ButtonsState.DELETE_VISIBLE_RIGHT)) {
                            if (buttonInstance[0].contains(x, event.getY())) {
                                buttonsActions.onDeleteButtonClicked(viewHolder.getAdapterPosition());
                            }

                            if (buttonInstance[1] != null && buttonInstance[1].contains(x, event.getY())) {
                                buttonsActions.onPromoteButtonClicked(viewHolder.getAdapterPosition());
                            }
                        }
                    }
                    buttonShowedState = ButtonsState.GONE;
                    currentItemViewHolder = null;
                }
                return false;
            }
        });
    }

    private void setItemsClickable(RecyclerView recyclerView, boolean isClickable) {
        for (int i = 0; i < recyclerView.getChildCount(); ++i) {
            recyclerView.getChildAt(i).setClickable(isClickable);
        }
    }

    private void drawButtons(Canvas c, RecyclerView.ViewHolder viewHolder) {
        float buttonWidthWithoutPadding = BUTTON_WIDTH;
        float corners = 0;

        View itemView = viewHolder.itemView;
        Paint p = new Paint();

        RectF editButton = new RectF(itemView.getLeft(), itemView.getTop(),
                itemView.getLeft() + buttonWidthWithoutPadding, itemView.getBottom());
        p.setColor(Color.BLUE);
        c.drawRoundRect(editButton, corners, corners, p);
        drawText("EDIT", c, editButton, p);

        RectF deleteButton = new RectF(itemView.getRight() - buttonWidthWithoutPadding / 2,
                itemView.getTop(), itemView.getRight(), itemView.getBottom());
        p.setColor(Color.RED);
        c.drawRoundRect(deleteButton, corners, corners, p);
        if (user.getId().equals(firebaseUser.getUid())) {
            drawText("Leave", c, deleteButton, p);
        } else {
            drawText("Remove", c, deleteButton, p);
        }

        RectF promoteButton = null;
        if (firebaseUser.getUid().equals(adminGroup)) {
            promoteButton = new RectF(itemView.getRight() - buttonWidthWithoutPadding,
                    itemView.getTop(), itemView.getRight() - buttonWidthWithoutPadding / 2, itemView.getBottom());
            p.setColor(Color.BLUE);
            c.drawRoundRect(promoteButton, corners, corners, p);
            drawText("Promote", c, promoteButton, p);
        }

        buttonInstance[0] = null;
        buttonInstance[1] = null;
        if (buttonShowedState == ButtonsState.EDIT_VISIBLE_RIGHT) {
            buttonInstance[0] = editButton;
        } else if (buttonShowedState == ButtonsState.DELETE_VISIBLE_RIGHT) {
            buttonInstance[0] = deleteButton;
            buttonInstance[1] = promoteButton;
        }
    }

    private void drawText(String text, Canvas c, RectF button, Paint p) {
        float textSize = 45;
        p.setColor(Color.WHITE);
        p.setAntiAlias(true);
        p.setTextSize(textSize);

        float textWidth = p.measureText(text);
        c.drawText(text, button.centerX() - (textWidth / 2), button.centerY() + (textSize / 2), p);
    }

    public void onDraw(Canvas c) {
        if (currentItemViewHolder != null) {
            drawButtons(c, currentItemViewHolder);
        }
    }

    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private float x = 0; // tránh tình trạng khi vuốt mà điểm dừng cuối cùng nằm trong nút xóa ( điểm bắt đầu ngoài nút xóa)
    private String adminGroup = "";
    private User user;
    private boolean swipeBack = false;
    private ButtonsState buttonShowedState = ButtonsState.GONE;
    private RectF buttonInstance[] = new RectF[5];
    private RecyclerView.ViewHolder currentItemViewHolder = null;
    private SwipeControllerActions buttonsActions = null;
    private static final float BUTTON_WIDTH = 380;
}
