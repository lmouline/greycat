package org.mwg.ml.common.matrix;

public class DefaultMatrixEngine {

    private static MatrixEngine _defaultEngine = null;

    /**
     * @native ts
     * if(DefaultMatrixEngine._defaultEngine == null){
     * DefaultMatrixEngine._defaultEngine = new org.mwg.ml.common.matrix.HybridMatrixEngine();
     * }
     * return DefaultMatrixEngine._defaultEngine;
     */
    public static MatrixEngine defaultEngine() {
        if (_defaultEngine == null) {
            _defaultEngine = new HybridMatrixEngine();
        }
        return _defaultEngine;
    }

}
