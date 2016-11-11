package org.mwg.ml.common.matrix;

public class DefaultMatrixEngine {

    private static MatrixEngine _defaultEngine = null;
    
    public static MatrixEngine defaultEngine() {
        if (_defaultEngine == null) {
            _defaultEngine = new HybridMatrixEngine();
        }
        return _defaultEngine;
    }

}
