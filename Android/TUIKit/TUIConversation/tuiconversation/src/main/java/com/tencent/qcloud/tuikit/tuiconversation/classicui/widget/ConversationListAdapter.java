package com.tencent.qcloud.tuikit.tuiconversation.classicui.widget;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.tencent.qcloud.tuikit.timcommon.util.ScreenUtil;
import com.tencent.qcloud.tuikit.tuiconversation.R;
import com.tencent.qcloud.tuikit.tuiconversation.TUIConversationService;
import com.tencent.qcloud.tuikit.tuiconversation.bean.ConversationInfo;
import com.tencent.qcloud.tuikit.tuiconversation.classicui.interfaces.OnConversationAdapterListener;
import com.tencent.qcloud.tuikit.tuiconversation.commonutil.ConversationUtils;
import com.tencent.qcloud.tuikit.tuiconversation.interfaces.IConversationListAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConversationListAdapter extends RecyclerView.Adapter implements IConversationListAdapter {
    public static final int ITEM_TYPE_HEADER_SEARCH = 101;
    public static final int ITEM_TYPE_FOOTER_LOADING = -99;
    public static final int ITEM_TYPE_NULL_DATA = 102;
    public static final int HEADER_COUNT = 1;
    public static final int FOOTER_COUNT = 1;
    public static final int SELECT_COUNT = 1;
    public static final int SELECT_LABEL_COUNT = 1;

    private boolean mHasShowUnreadDot = true;
    protected List<ConversationInfo> mDataSource = new ArrayList<>();
    private OnConversationAdapterListener mOnConversationAdapterListener;

    private final HashMap<String, Boolean> mSelectedPositions = new HashMap<>();
    private boolean isShowMultiSelectCheckBox = false;
    private boolean isForwardFragment = false;
    private boolean isOnlyConversationSelect = false;
    protected List<String> mCannotSelectIds = new ArrayList<>();

    private boolean mIsLoading = false;

    private boolean isClick = false;
    private int currentPosition = -1;

    private View searchView;
    
    private boolean showFoldedStyle = true;

    private String conversationGroupName = ConversationUtils.getConversationAllGroupName();

    public ConversationListAdapter() {
        mCannotSelectIds.clear();
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public boolean isClick() {
        return isClick;
    }

    public void setClick(boolean click) {
        isClick = click;
    }

    public void setCannotSelectIds(List<String> list) {
        this.mCannotSelectIds = list;
    }

    private boolean isInCannotSelectIds(String id) {
        if (mCannotSelectIds.isEmpty()) {
            return false;
        }
        if (mCannotSelectIds.contains(id)) {
            return true;
        } else {
            return false;
        }
    }

    public void setCurrentPosition(int currentPosition, boolean isClick) {
        this.currentPosition = currentPosition;
        this.isClick = isClick;
    }

    public void setShowMultiSelectCheckBox(boolean show) {
        isShowMultiSelectCheckBox = show;

        if (!isShowMultiSelectCheckBox) {
            mSelectedPositions.clear();
        }
    }

    public void setForwardFragment(boolean forwardFragment) {
        isForwardFragment = forwardFragment;
    }

    public void setIsOnlyConversationSelect(boolean is) {
        this.isOnlyConversationSelect = is;
    }

    public void setSearchView(View searchView) {
        this.searchView = searchView;
    }

    public void setShowFoldedStyle(boolean showFoldedStyle) {
        this.showFoldedStyle = showFoldedStyle;
    }

    public void setItemChecked(String conversationId, boolean isChecked) {
        mSelectedPositions.put(conversationId, isChecked);
    }

    private boolean isItemChecked(String id) {
        if (mSelectedPositions.size() <= 0) {
            return false;
        }

        if (mSelectedPositions.containsKey(id)) {
            return mSelectedPositions.get(id);
        } else {
            return false;
        }
    }

    public List<ConversationInfo> getSelectedItem() {
        if (mSelectedPositions.size() == 0) {
            return null;
        }
        List<ConversationInfo> selectList = new ArrayList<>();
        for (int i = 0; i < getItemCount() - 1; i++) {
            ConversationInfo conversationInfo = getItem(i);
            if (conversationInfo == null) {
                continue;
            }
            if (isItemChecked(conversationInfo.getConversationId())) {
                selectList.add(conversationInfo);
            }
        }
        return selectList;
    }

    public void setSelectConversations(List<ConversationInfo> dataSource) {
        if (dataSource == null || dataSource.size() == 0) {
            mSelectedPositions.clear();
            notifyDataSetChanged();
            return;
        }

        mSelectedPositions.clear();
        for (int i = 0; i < dataSource.size(); i++) {
            for (int j = 0; j < mDataSource.size(); j++) {
                if (TextUtils.equals(dataSource.get(i).getConversationId(), mDataSource.get(j).getConversationId())) {
                    setItemChecked(mDataSource.get(j).getConversationId(), true);
                    notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    public void setOnConversationAdapterListener(OnConversationAdapterListener listener) {
        this.mOnConversationAdapterListener = listener;
    }

    public void setConversationGroupName(String groupName) {
        this.conversationGroupName = groupName;
    }

    private boolean isShowNullConversationText() {
        if (mDataSource.isEmpty() && showFoldedStyle) {
            return true;
        }

        return false;
    }

    @Override
    public void onDataSourceChanged(List<ConversationInfo> dataSource) {
        this.mDataSource = dataSource;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ConversationBaseHolder holder = null;
        
        View view;
        
        if (viewType == ITEM_TYPE_HEADER_SEARCH) {
            
            if (searchView == null) {
                searchView = new View(parent.getContext());
            }
            return new HeaderViewHolder(searchView);
        } else if (viewType == ConversationInfo.TYPE_CUSTOM) {
            view = inflater.inflate(R.layout.conversation_custom_adapter, parent, false);
            holder = new ConversationCustomHolder(view);
        } else if (viewType == ITEM_TYPE_FOOTER_LOADING) {
            view = inflater.inflate(R.layout.conversation_loading_progress_bar, parent, false);
            return new FooterViewHolder(view);
        } else if (viewType == ConversationInfo.TYPE_FORWAR_SELECT) {
            view = inflater.inflate(R.layout.conversation_forward_select_adapter, parent, false);
            return new ForwardSelectHolder(view);
        } else if (viewType == ConversationInfo.TYPE_RECENT_LABEL) {
            view = inflater.inflate(R.layout.conversation_forward_label_adapter, parent, false);
            return new ForwardLabelHolder(view);
        } else if (viewType == ITEM_TYPE_NULL_DATA) {
            view = inflater.inflate(R.layout.conversation_null_layout, parent, false);
            return new RecyclerView.ViewHolder(view) {};
        } else {
            view = inflater.inflate(R.layout.conversation_list_item_layout, parent, false);
            holder = new ConversationCommonHolder(view);
            ((ConversationCommonHolder) holder).setForwardMode(isForwardFragment);
            ((ConversationCommonHolder) holder).setShowFoldedStyle(showFoldedStyle);
        }
        holder.setAdapter(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final ConversationInfo conversationInfo = getItem(position);
        ConversationBaseHolder baseHolder = null;
        if (conversationInfo != null) {
            baseHolder = (ConversationBaseHolder) holder;
        }

        switch (getItemViewType(position)) {
            case ConversationInfo.TYPE_CUSTOM:
            case ConversationInfo.TYPE_RECENT_LABEL:
                break;
            case ITEM_TYPE_NULL_DATA:
                TextView nullTextView = holder.itemView.findViewById(R.id.conversation_null_text);
                nullTextView.setText(holder.itemView.getResources().getString(R.string.conversation_null_text, conversationGroupName));
                return;
            case ITEM_TYPE_FOOTER_LOADING: {
                if (holder instanceof FooterViewHolder) {
                    ((ConversationBaseHolder) holder).layoutViews(null, position);
                }
                break;
            }
            case ConversationInfo.TYPE_FORWAR_SELECT: {
                ForwardSelectHolder selectHolder = (ForwardSelectHolder) holder;
                selectHolder.refreshTitle(!isShowMultiSelectCheckBox);
                setOnClickListener(holder, getItemViewType(position), conversationInfo);
                break;
            }
            default:
                setOnClickListener(holder, getItemViewType(position), conversationInfo);
        }

        if (baseHolder != null) {
            if (getCurrentPosition() == position && isClick()) {
                baseHolder.itemView.setBackgroundResource(R.color.conversation_item_clicked_color);
            }
            baseHolder.layoutViews(conversationInfo, position);
            setCheckBoxStatus(conversationInfo, position, baseHolder);
        }
    }

    private void setOnClickListener(RecyclerView.ViewHolder holder, int viewType, ConversationInfo conversationInfo) {
        if (holder instanceof HeaderViewHolder) {
            return;
        }
        
        if (mOnConversationAdapterListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnConversationAdapterListener.onItemClick(view, viewType, conversationInfo);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mOnConversationAdapterListener.onItemLongClick(view, conversationInfo);
                    int position = getIndexInAdapter(conversationInfo);
                    if (position != -1) {
                        setCurrentPosition(position, true);
                        notifyItemChanged(position);
                    }
                    return true;
                }
            });
        }
    }

    
    private void setCheckBoxStatus(final ConversationInfo conversationInfo, int position, ConversationBaseHolder baseHolder) {
        if (!(baseHolder instanceof ConversationCommonHolder) || ((ConversationCommonHolder) baseHolder).multiSelectCheckBox == null) {
            return;
        }
        int viewType = getItemViewType(position);
        String conversationId = conversationInfo.getConversationId();
        ConversationCommonHolder commonHolder = (ConversationCommonHolder) baseHolder;
        if (!isShowMultiSelectCheckBox) {
            commonHolder.multiSelectCheckBox.setVisibility(View.GONE);
        } else {
            commonHolder.multiSelectCheckBox.setVisibility(View.VISIBLE);

            if (isInCannotSelectIds(conversationId)) {
                commonHolder.itemView.setEnabled(false);
                commonHolder.multiSelectCheckBox.setEnabled(false);
                commonHolder.itemView.setAlpha(0.5f);
                commonHolder.multiSelectCheckBox.setChecked(true);
            } else {
                commonHolder.itemView.setEnabled(true);
                commonHolder.multiSelectCheckBox.setEnabled(true);
                commonHolder.itemView.setAlpha(1f);
                
                commonHolder.multiSelectCheckBox.setChecked(isItemChecked(conversationId));
            }
            
            commonHolder.multiSelectCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setItemChecked(conversationId, !isItemChecked(conversationId));
                    if (mOnConversationAdapterListener != null) {
                        mOnConversationAdapterListener.onItemClick(v, viewType, conversationInfo);
                    }
                }
            });

            
            baseHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setItemChecked(conversationId, !isItemChecked(conversationId));
                    int currentPosition = getIndexInAdapter(conversationInfo);
                    if (currentPosition != -1) {
                        notifyItemChanged(currentPosition);
                    }
                    if (mOnConversationAdapterListener != null) {
                        mOnConversationAdapterListener.onItemClick(v, viewType, conversationInfo);
                    }
                }
            });
        }
    }

    public int getIndexInAdapter(ConversationInfo conversationInfo) {
        int position = -1;
        if (mDataSource != null) {
            int indexInData = mDataSource.indexOf(conversationInfo);
            if (indexInData != -1) {
                position = getItemIndexInAdapter(indexInData);
            }
        }
        return position;
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        
        if (holder instanceof ConversationCommonHolder) {
            ((ConversationCommonHolder) holder).conversationIconView.clearImage();
        }
    }

    public ConversationInfo getItem(int position) {
        if (mDataSource.isEmpty() || position == getItemCount() - 1) {
            return null;
        }

        int dataPosition;
        if (isForwardFragment) {
            if (isOnlyConversationSelect) {
                dataPosition = position - SELECT_LABEL_COUNT;
            } else {
                dataPosition = position - SELECT_COUNT - SELECT_LABEL_COUNT;
            }
        } else {
            dataPosition = position - HEADER_COUNT;
        }
        if (dataPosition < mDataSource.size() && dataPosition >= 0) {
            return mDataSource.get(dataPosition);
        }

        return null;
    }

    @Override
    public int getItemCount() {
        int listSize = mDataSource.size();
        if (isForwardFragment) {
            if (isOnlyConversationSelect) {
                return listSize + SELECT_LABEL_COUNT + FOOTER_COUNT;
            }
            return listSize + SELECT_COUNT + SELECT_LABEL_COUNT + FOOTER_COUNT;
        } else if (isShowNullConversationText()) {
            return 1;
        }
        return listSize + HEADER_COUNT + FOOTER_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        if (isForwardFragment) {
            if (isOnlyConversationSelect) {
                if (position == 0) {
                    return ConversationInfo.TYPE_RECENT_LABEL;
                }
            } else {
                if (position == 0) {
                    return ConversationInfo.TYPE_FORWAR_SELECT;
                } else if (position == 1) {
                    return ConversationInfo.TYPE_RECENT_LABEL;
                }
            }
        } else if (isShowNullConversationText()) {
            return ITEM_TYPE_NULL_DATA;
        } else {
            if (position == 0) {
                return ITEM_TYPE_HEADER_SEARCH;
            }
        }
        if (position == getItemCount() - 1) {
            return ITEM_TYPE_FOOTER_LOADING;
        } else if (mDataSource != null) {
            ConversationInfo conversationInfo = getItem(position);
            if (conversationInfo != null) {
                return conversationInfo.getType();
            }
        }
        return ConversationInfo.TYPE_COMMON;
    }

    private int getItemIndexInAdapter(int index) {
        int itemIndex;
        if (isForwardFragment) {
            if (isOnlyConversationSelect) {
                itemIndex = index + SELECT_LABEL_COUNT;
            } else {
                itemIndex = index + SELECT_LABEL_COUNT + SELECT_COUNT;
            }
        } else {
            itemIndex = index + HEADER_COUNT;
        }
        return itemIndex;
    }

    @Override
    public void onItemRemoved(int position) {
        int itemIndex = getItemIndexInAdapter(position);
        notifyItemRemoved(itemIndex);
    }

    @Override
    public void onItemInserted(int position) {
        int itemIndex = getItemIndexInAdapter(position);
        notifyItemInserted(itemIndex);
    }

    @Override
    public void onItemMoved(int fromPosition, int toPosition) {
        int fromIndex = getItemIndexInAdapter(fromPosition);
        int toIndex = getItemIndexInAdapter(toPosition);
        notifyItemMoved(fromIndex, toIndex);
    }

    @Override
    public void onItemChanged(int position) {
        int itemIndex = getItemIndexInAdapter(position);
        notifyItemChanged(itemIndex);
    }

    @Override
    public void onItemRangeChanged(int startPosition, int count) {
        int itemStartIndex = getItemIndexInAdapter(startPosition);
        notifyItemRangeChanged(itemStartIndex, count);
    }

    @Override
    public void onConversationChanged(List<ConversationInfo> conversationInfoList) {
        if (mOnConversationAdapterListener != null) {
            mOnConversationAdapterListener.onConversationChanged(conversationInfoList);
        }
    }

    public void disableItemUnreadDot(boolean flag) {
        mHasShowUnreadDot = !flag;
    }

    public boolean hasItemUnreadDot() {
        return mHasShowUnreadDot;
    }

    @Override
    public void onLoadingStateChanged(boolean isLoading) {
        this.mIsLoading = isLoading;
        notifyItemChanged(getItemCount() - 1);
    }

    @Override
    public void onViewNeedRefresh() {
        notifyDataSetChanged();
    }

    // header
    static class HeaderViewHolder extends RecyclerView.ViewHolder {

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    // footer
    class FooterViewHolder extends ConversationBaseHolder {

        public FooterViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void layoutViews(ConversationInfo conversationInfo, int position) {
            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams) rootView.getLayoutParams();
            if (mIsLoading) {
                param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                param.width = LinearLayout.LayoutParams.MATCH_PARENT;
                rootView.setVisibility(View.VISIBLE);
            } else {
                param.height = 0;
                param.width = 0;
                rootView.setVisibility(View.GONE);
            }
            rootView.setLayoutParams(param);
        }
    }

    static class ForwardLabelHolder extends ConversationBaseHolder {

        public ForwardLabelHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void layoutViews(ConversationInfo conversationInfo, int position) {}
    }

    static class ForwardSelectHolder extends ConversationBaseHolder {
        private final TextView titleView;

        public ForwardSelectHolder(View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.forward_title);
        }

        @Override
        public void layoutViews(ConversationInfo conversationInfo, int position) {}

        public void refreshTitle(boolean isCreateGroup) {
            if (titleView == null) {
                return;
            }

            if (isCreateGroup) {
                titleView.setText(TUIConversationService.getAppContext().getString(R.string.forward_select_new_chat));
            } else {
                titleView.setText(TUIConversationService.getAppContext().getString(R.string.forward_select_from_contact));
            }
        }
    }
}
