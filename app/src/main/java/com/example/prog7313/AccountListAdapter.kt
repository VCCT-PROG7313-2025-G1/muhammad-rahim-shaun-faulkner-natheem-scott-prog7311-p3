package com.example.prog7313

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AccountListAdapter (

    private var accounts: MutableList<AccountData>,
    private val onEditClick: (AccountData) -> Unit,
    private val onDeleteClick: (AccountData) -> Unit
    ) : RecyclerView.Adapter<AccountListAdapter.AccountViewHolder>() {

        inner class AccountViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val bankName: TextView = itemView.findViewById(R.id.tvBankName)
            val accountType: TextView = itemView.findViewById(R.id.tvAccountType)
            val balance: TextView = itemView.findViewById(R.id.tvBalance)
            val editButton: Button = itemView.findViewById(R.id.btnEdit)
            val deleteButton: Button = itemView.findViewById(R.id.btnDelete)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_account, parent, false)
            return AccountViewHolder(view)
        }

        override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
            val account = accounts[position]
            android.util.Log.i("AccountListAdapter", "Binding account: ${account.bankName} at position $position")
            holder.bankName.text = account.bankName
            holder.accountType.text = account.accountType
            holder.balance.text = "Balance: R %.2f".format(account.balance)

            holder.editButton.setOnClickListener { onEditClick(account) }
            holder.deleteButton.setOnClickListener { onDeleteClick(account) }
        }

        override fun getItemCount(): Int = accounts.size

        fun updateList(newAccounts: List<AccountData>) {
            accounts.clear()
            accounts.addAll(newAccounts)
            notifyDataSetChanged()
        }
    }