package com.dak.wrote.backend.implementations.dao.exceptions

import java.lang.IllegalStateException

class UnknownImplementationException(message: String) : IllegalStateException(message)