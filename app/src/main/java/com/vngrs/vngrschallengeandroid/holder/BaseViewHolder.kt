package com.vngrs.vngrschallengeandroid.holder

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vngrs.vngrschallengeandroid.R
import com.vngrs.vngrschallengeandroid.model.BaseListModel

open class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    companion object {
        fun viewHolderfactory(inflater: LayoutInflater, parent: ViewGroup, type: Int) : BaseViewHolder {
            return when (type) {
                BaseListModel.TYPE_TEXT -> TextTweetViewHolder(inflater.inflate(R.layout.list_item_search_regular, parent, false))
                else -> {
                    TextTweetViewHolder(inflater.inflate(R.layout.list_item_search_regular, parent, false))
                }
            }
        }
    }

}