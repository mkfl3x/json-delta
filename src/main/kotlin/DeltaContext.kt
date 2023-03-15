class DeltaContext {

    private val equality: Boolean
        get() = mismatches.isEmpty()

    private val mismatches = mutableMapOf<String, MutableList<Mismatch>>()

    fun areEquals() = equality

    fun addMismatch(field: String, mismatch: Mismatch) {
        if (mismatches[field] == null)
            mismatches[field] = mutableListOf()
        mismatches[field]!!.add(mismatch)
    }

    fun getMismatches() = mismatches
}
