import scala.util.parsing.combinator.syntactical._

object TurtleDSL extends StandardTokenParsers {
  lexical.delimiters ++= List("(", ")", ",")
  lexical.reserved += ("buy", "sell", "shares", "at", "max", "min", "for", "trading", "account")

  def instr = trans ~ account_spec

  def trans = "(" ~> repsep(trans_spec, ",") <~ ")"

  def trans_spec = buy_sell ~ buy_sell_instr

  def account_spec = "for" ~> "trading" ~> "account" ~> stringLit

  def buy_sell = ("buy" | "sell")

  def buy_sell_instr = security_spec ~ price_spec

  def security_spec = numericLit ~ ident ~ "shares"

  def price_spec = "at" ~ ("min" | "max") ~ numericLit
}