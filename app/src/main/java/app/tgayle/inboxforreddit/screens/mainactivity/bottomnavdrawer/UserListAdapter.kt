package app.tgayle.inboxforreddit.screens.mainactivity.bottomnavdrawer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import app.tgayle.inboxforreddit.R
import app.tgayle.inboxforreddit.model.RedditUsernameAndUnreadMessageCount
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.user_list_item.*

typealias OnItemClickListener = (user: RedditUsernameAndUnreadMessageCount?) -> Unit
typealias OnAddUserClick = () -> Unit

class UserListAdapter: PagedListAdapter<RedditUsernameAndUnreadMessageCount, UserListAdapter.UserListViewHolder>(DIFF_UTIL) {
    var onUserClick: OnItemClickListener? = null
    var onUserRemoveClick: OnItemClickListener? = null
    var onUserAddClick: OnAddUserClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return UserListViewHolder(inflater.inflate(R.layout.user_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: UserListViewHolder, position: Int) {
         holder.bind(getItem(position), onUserClick, onUserRemoveClick)
    }

    class UserListViewHolder(override val containerView: View): RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(user: RedditUsernameAndUnreadMessageCount?, onUserClick: OnItemClickListener?, onUserRemoveClick: OnItemClickListener?) {
            user_list_item_parent.setOnClickListener {
                onUserClick?.invoke(user)
            }

            user.let {
                if (it == null) {
                    user_list_item_username.text = "Add account"
                    user_list_item_unread_count.text = null
                    user_list_item_remove_account.visibility = View.GONE
                } else {
                    user_list_item_username.text = it.username
                    user_list_item_unread_count.text = it.numUnreadMessages.toString()
                    user_list_item_remove_account.visibility = View.VISIBLE
                    user_list_item_remove_account.setOnClickListener {view ->
                        Snackbar.make(view, "${it.username} clicked", Snackbar.LENGTH_SHORT).show()
                        onUserRemoveClick?.invoke(user)
                    }
                }
            }
        }
    }

    companion object {
        val DIFF_UTIL = object: DiffUtil.ItemCallback<RedditUsernameAndUnreadMessageCount?>() {
            override fun areItemsTheSame(oldItem: RedditUsernameAndUnreadMessageCount, newItem: RedditUsernameAndUnreadMessageCount
            ): Boolean {
                return oldItem.username == newItem.username
            }

            override fun areContentsTheSame(oldItem: RedditUsernameAndUnreadMessageCount, newItem: RedditUsernameAndUnreadMessageCount): Boolean {
                return oldItem.numUnreadMessages == newItem.numUnreadMessages
            }
        }
    }
}