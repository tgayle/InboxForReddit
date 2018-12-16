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

class UserListAdapter(var users: List<RedditUsernameAndUnreadMessageCount> = arrayListOf()): RecyclerView.Adapter<UserListAdapter.UserListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListViewHolder {
        return UserListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.user_list_item, parent, false))
    }

    override fun getItemCount() = users.size

    override fun onBindViewHolder(holder: UserListViewHolder, position: Int) {
        holder.bind(users[position])
    }

    fun submitItems(newUsers: List<RedditUsernameAndUnreadMessageCount>) {
        val originalSize = newUsers.size
        notifyItemRangeRemoved(0, originalSize)
        users = newUsers
        notifyItemRangeInserted(0, newUsers.size)
    }


    class UserListViewHolder(override val containerView: View): RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(user: RedditUsernameAndUnreadMessageCount) {
            user_list_item_username.text = user.username
            user_list_item_unread_count.text = user.numUnreadMessages.toString()
            user_list_item_remove_account.setOnClickListener {
                Snackbar.make(it, "${user.username} clicked", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}