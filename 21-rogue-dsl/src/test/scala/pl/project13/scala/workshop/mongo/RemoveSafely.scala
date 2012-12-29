package pl.project13.scala.workshop.mongo

trait RemoveSafely {
  def deleteIterableSafely[Deletable <: {def delete_! : Boolean}](del: Iterable[Deletable]) {
    del.foreach { deleteSafely _ }
  }

  def deleteSafely[Deletable <: {def delete_! : Boolean}](del: Deletable*) {
    del.foreach { deleteSafely _ }
  }

  def deleteSafely[Deletable <: {def delete_! : Boolean}](del: Deletable) {
    if(del != null) del.delete_!
  }
}
