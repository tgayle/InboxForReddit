package app.tgayle.inboxforreddit.screens.mainactivity.bottomnavdrawer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.tgayle.inboxforreddit.R
import app.tgayle.inboxforreddit.model.RedditUsernameAndUnreadMessageCount
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.user_list_item.*

typealias OnItemClickListener = (position: Int, totalSize: Int, user: RedditUsernameAndUnreadMessageCount?) -> Unit

class UserListAdapter(var users: List<RedditUsernameAndUnreadMessageCount> = arrayListOf()): RecyclerView.Adapter<UserListAdapter.UserListViewHolder>() {
    private var onUserClick: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListViewHolder {
        return UserListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.user_list_item, parent, false))
    }

    override fun getItemCount() = users.size + 1

    override fun onBindViewHolder(holder: UserListViewHolder, position: Int) {
        holder.bind(position, itemCount, users.getOrNull(position), onUserClick)
    }

    fun submitItems(newUsers: List<RedditUsernameAndUnreadMessageCount>) {
        val originalSize = newUsers.size
        notifyItemRangeRemoved(0, originalSize)
        users = newUsers
        notifyItemRangeInserted(0, newUsers.size)
    }

    fun setOnClickListener(block: OnItemClickListener) {
        onUserClick = block
    }

    class UserListViewHolder(override val containerView: View): RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(position: Int, maxSize: Int, user: RedditUsernameAndUnreadMessageCount?, onUserClick: OnItemClickListener?) {
            user_list_item_parent.setOnClickListener {
                onUserClick?.invoke(position, maxSize, user)
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
                    }
                }
            }
        }
    }
}