const webpack = require('webpack');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const ExtractTextPlugin = require('extract-text-webpack-plugin');
const CopyWebpackPlugin = require('copy-webpack-plugin');
const merge = require('webpack-merge');
const webpackBaseConfig = require('./webpack.base.config.js');
const fs = require('fs');
const package = require('../package.json');
const ip = require('ip').address()

fs.open('./build/env.js', 'w', function (err, fd) {
    const buf = 'export default "development";';
    fs.write(fd, buf, 0, 'utf-8', function (err, written, buffer) {});
});

module.exports = merge(webpackBaseConfig, {
    devtool: '#source-map',
    devServer: {
        port: 80,
        host: "14.225.253.13",
        proxy: {
            '/admin/': {
                target: '14.225.253.13',
                changeOrigin: true
            },
            '/swap/': {
                target: '14.225.253.13',
                changeOrigin: true,
                ws:true
            }
        }
    },
    output: {
        publicPath: '/dist/',
        filename: '[name].js',
        chunkFilename: '[name].chunk.js'
    },
    plugins: [
        new ExtractTextPlugin({
            filename: '[name].css',
            allChunks: true
        }),
        new webpack.optimize.CommonsChunkPlugin({
            name: ['vender-exten', 'vender-base'],
            minChunks: Infinity
        }),
        new HtmlWebpackPlugin({
            title: '管理后台 - KICKSTARTERKIT|全球数字资产交易平台',
            filename: '../index.html',
            template: './src/template/index.ejs',
            favicon: './favicon.ico',
            inject: false
        }),
        new CopyWebpackPlugin([{
            from: 'src/views/main-components/theme-switch/theme'
        }, ], {
            ignore: [
                //'text-editor.vue'
            ]
        })
    ]
});