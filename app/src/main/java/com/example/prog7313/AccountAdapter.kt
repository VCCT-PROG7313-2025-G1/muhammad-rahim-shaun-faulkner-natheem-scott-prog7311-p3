package com.example.prog7313

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AccountAdapter(private val accounts: List<AccountData>) :
    RecyclerView.Adapter<AccountAdapter.AccountViewHolder>() {

    class AccountViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.text_account_name)
        val type: TextView = itemView.findViewById(R.id.text_account_type)
        val balance: TextView = itemView.findViewById(R.id.text_account_balance)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_account_card_home, parent, false)
        return AccountViewHolder(view)
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        val accountData = accounts[position]
        holder.name.text = accountData.bankName
        holder.type.text = "${accountData.accountType}"
        holder.balance.text = "R ${"%.2f".format(accountData.balance)}"
    }

    override fun getItemCount(): Int = accounts.size
}