package com.bloomimpact.bancus.common.utils

import com.bloomimpact.bancus.BuildConfig

class Constants {
    companion object {

        //QUESTION IDS
        const val EXPIRY_DATE_QUESTION = 30
        const val GUARANTOR_QUESTION = 30
        const val NO_OF_LOCATIONS_QUESTION = 20
        const val PERSONAL_DOB_DATE_QUESTION = 35
        const val PERSONAL_NAME_QUESTION = 33
        const val PRE_QUALIFIER_BUSINESS_START_DATE_QUESTION = 7
        const val PRE_QUALIFIER_SALES_REVENUE_QUESTION = 4
        const val BUSINESS_REGISTRATION_QUESTION = 6
        const val AUDITED_STATEMENT_QUESTION = 25
        const val TIN_QUESTION = 60
        const val REG_CERTIFICATE_QUESTION = 27
        const val VAT_PICTURE_QUESTION = 28
        const val PRE_QUALIFIER_LOANS_HOW_DID_YOU_HEAR_ABOUT_US_QUESTION = 67
        const val PRE_QUALIFIER_SAVINGS_HOW_DID_YOU_HEAR_ABOUT_US_QUESTION = 68
        const val PRE_QUALIFIER_CURRENT_HOW_DID_YOU_HEAR_ABOUT_US_QUESTION = 69

        //LOANS
        const val LOAN_PRE_QUALIFIER_QUESTION_AMOUNT_BORROWED = 1
        const val LOAN_BUSINESS_QUESTION_BUSINESS_NAME = 15
        const val LOAN_QUESTION_PHONE_NUMBER = 62
        //SAVINGS
        const val SAVINGS_PRE_QUALIFIER_QUESTION_BUSINESS_NAME = 12
        const val SAVINGS_QUESTION_PHONE_NUMBER = 63
        //CURRENT
        const val CURRENT_PRE_QUALIFIER_QUESTION_BUSINESS_NAME = 48
        const val CURRENT_QUESTION_PHONE_NUMBER = 65
        const val CURRENT_QUESTION_SELFIE = 40
        const val CURRENT_PROOF_OF_HOME_ADDRESS = 41
        const val CURRENT_MAP_OF_HOME_ADDRESS = 42
        //INVESTMENT
        val INVESTMENTS_QUESTION_PHONE_NUMBER = 64

        //LEAD STATUS KEYS
        const val LEAD_STATUS_KEY_IN_PROGRESS = 1
        const val LEAD_STATUS_KEY_IN_BLOOM_REVIEW = 2
        const val LEAD_STATUS_KEY_WITH_FSP = 3
        const val LEAD_STATUS_KEY_COMPLETE = 4

        //LEAD STATUS LABELS
        const val LEAD_STATUS_IN_PROGRESS = "In Progress"
        const val LEAD_STATUS_IN_BLOOM_REVIEW = "Under Bloom Review"
        const val LEAD_STATUS_WITH_FSP = "With FSP"
        const val LEAD_STATUS_COMPLETE = "Complete"

        //Question Types
        const val QUESTION_TAG_MAIN = "main-"
        const val QUESTION_TAG_SUB = "sub-"
        const val QUESTION_TAG_SUB_SUB = "subsub-"

        //Answer Types
        const val ANSWER_TYPE_KEY_NUMBER = 0
        const val ANSWER_TYPE_KEY_SINGLE_SELECT = 1
        const val ANSWER_TYPE_KEY_MULTI_SELECT = 2
        const val ANSWER_TYPE_KEY_DATE = 3
        const val ANSWER_TYPE_KEY_TEXT = 4
        const val ANSWER_TYPE_KEY_IMAGE = 5
        const val ANSWER_TYPE_KEY_STATEMENT = 6
        const val ANSWER_TYPE_KEY_MULTI_ELEMENT = 7
        const val ANSWER_TYPE_KEY_AMOUNT = 8
        const val ANSWER_TYPE_KEY_EMAIL = 9
        const val ANSWER_TYPE_KEY_URL = 10
        const val ANSWER_TYPE_KEY_SIGNATURE = 11
        const val ANSWER_TYPE_KEY_PHONE_NUMBER = 12

        const val ANSWER_TYPE_NUMBER = "numeric"
        const val ANSWER_TYPE_SINGLE_SELECT = "select"
        const val ANSWER_TYPE_MULTI_SELECT = "multiselect"
        const val ANSWER_TYPE_DATE = "date"
        const val ANSWER_TYPE_TEXT = "text"
        const val ANSWER_TYPE_IMAGE = "image"
        const val ANSWER_TYPE_STATEMENT = "statement"
        const val ANSWER_TYPE_MULTI_ELEMENT = "multielement"
        const val ANSWER_TYPE_AMOUNT = "amount"
        const val ANSWER_TYPE_EMAIL = "email"
        const val ANSWER_TYPE_URL = "url"
        const val ANSWER_TYPE_SIGNATURE = "signature"
        const val ANSWER_TYPE_PHONE_NUMBER = "phone"
        const val ANSWER_TYPE_GEO = "geo"
        const val ANSWER_TYPE_DYNAMIC = "dynamic"

        //Credit Report View State
        const val CREDIT_REPORT_VIEW_STATE_MENU = 1
        const val CREDIT_REPORT_VIEW_STATE_BUSINESSES = 2
        const val CREDIT_REPORT_VIEW_STATE_CREDIT_REPORTS = 3

        //Sections
        const val SECTION_PRE_QUALIFIER = 1
        const val SECTION_BUSINESS = 2
        const val SECTION_PERSONAL = 3

        const val SECTION_PRE_QUALIFIER_LABEL = "Initial"
        const val SECTION_BUSINESS_LABEL = "About Your Business"
        const val SECTION_PERSONAL_LABEL = "About You"

        //Permissions
        const val LOAD_SETTINGS = "load_settings"
        const val PERMISSION_CALLBACK_CONSTANT = 100
        const val REQUEST_PERMISSION_SETTING = 101
        const val REQUEST_CHECK_SETTINGS = 102

        //Product Type Display Types
        const val TYPE_GRID = 1
        const val TYPE_LIST = 2

        //Product Type Keys
        const val PRODUCT_TYPE_KEY_LOAN = 1
        const val PRODUCT_TYPE_KEY_SAVINGS = 2
        const val PRODUCT_TYPE_KEY_CURRENT = 3
        const val PRODUCT_TYPE_KEY_CREDIT = 4

        //Product Type Labels
        const val PRODUCT_TYPE_LABEL_LOAN = "Loan"
        const val PRODUCT_TYPE_LABEL_SAVINGS = "Savings"
        const val PRODUCT_TYPE_LABEL_CURRENT = "Current"
        const val PRODUCT_TYPE_LABEL_CREDIT = "Credit"
        const val PRODUCT_TYPE_LABEL_ANY = "Any"

        //Tabs
        const val TAB_HOME = 0
        const val TAB_APPLICATIONS = 1
        const val TAB_MESSAGES = 2
        const val TAB_MORE = 3

        //Pin Code
        const val PIN_CODE_LENGTH = 4
        const val PIN_CODE_SET = 1
        const val PIN_CODE_CONFIRM = 2
        const val PIN_CODE_UPDATE = 3
        const val PIN_CODE_ENTER = 4

        //API
        private const val URL_SERVER = BuildConfig.SERVER_URL
        const val URL_REGISTER = URL_SERVER + "users"
        const val URL_LOGIN = URL_SERVER + "login"
        const val URL_USER_ORGANISATIONS_CHECKSUM = URL_SERVER + "checksum/userorg/"
        const val URL_USER_ORGANISATIONS = URL_SERVER + "organizations/byuser/"
        const val URL_GET_USER_APPLICATIONS = URL_SERVER + "applications/user/"
        const val URL_COUNTRIES = URL_SERVER + "countries"
        const val URL_COUNTRIES_ACTIVE_CHECKSUM = URL_SERVER + "checksum/countries/active"
        const val URL_COUNTRIES_ACTIVE = URL_SERVER + "countries/active"
        const val URL_FAQS_CHECKSUM = URL_SERVER + "checksum/faqs"
        const val URL_FAQS = URL_SERVER + "faqs"
        const val URL_PRODUCT_TYPES_CHECKSUM = URL_SERVER + "checksum/productTypes"
        const val URL_PRODUCT_TYPES = URL_SERVER + "products/types"
        const val URL_QUESTIONS_CHECKSUM = URL_SERVER + "checksum/questions/active"
        const val URL_QUESTIONS = URL_SERVER + "questions/active"
        const val URL_ANSWERS_CHECKSUM = URL_SERVER + "checksum/questionsAnswers/"
        const val URL_ANSWERS = URL_SERVER + "questions/answers/"
        const val URL_SUBMIT_APPLICATION = URL_SERVER + "applications/store"
        const val URL_SUBMIT_APPLICATION_UPDATE = URL_SERVER + "applications/"
        const val URL_DELETE_APPLICATION = URL_SERVER + "applications/"
        const val URL_IMAGE_UPLOAD = URL_SERVER + "profiles/user/documents"
        const val CODE_ERROR = "error"
        const val CODE_SUCCESS = "success"
        const val BLOOM_VIDEO_URL = "http://www.bloomimpact.net/wp-content/uploads/2017/04/Bloom_final_fullHD.mp4"

        //Broadcasts
        const val Broadcast_LOAD_FRAGMENT_BROADCAST = "com.bloomimpact.bancus.LOAD_FRAGMENT_BROADCAST"
        const val Broadcast_VALIDATE_PIN_CODE = "com.bloomimpact.bancus.VALIDATE_PIN_CODE"
        const val Broadcast_SET_SELECTED_TAB = "com.bloomimpact.bancus.SET_SELECTED_TAB"
        const val Broadcast_COMPLETE_APP_DATA_SYNC = "com.bloomimpact.bancus.COMPLETE_APP_DATA_SYNC"
        const val Broadcast_COMPLETE_APPLICATIONS_SYNC = "com.bloomimpact.bancus.COMPLETE_APPLICATIONS_SYNC"
        const val Broadcast_MOVE_TO_QUESTION = "com.bloomimpact.bancus.MOVE_TO_QUESTION"
        const val Broadcast_IS_QUESTION_RESPONSE_VALIDATED = "com.bloomimpact.bancus.IS_QUESTION_RESPONSE_VALIDATED"
        const val Broadcast_GO_TO_NEXT_SECTION = "com.bloomimpact.bancus.GO_TO_NEXT_SECTION"
        const val Broadcast_DISMISS_KEYBOARD = "com.bloomimpact.bancus.DISMISS_KEYBOARD"
        const val Broadcast_SHOW_HOME_TAB_CHILD_FRAGMENT = "com.bloomimpact.bancus.SHOW_HOME_TAB_CHILD_FRAGMENT"
        const val Broadcast_TOGGLE_BACK_BUTTON_VISIBILITY = "com.bloomimpact.bancus.TOGGLE_BACK_BUTTON_VISIBILITY"
        const val Broadcast_COMPLETE_SAVE_AND_EXIT = "com.bloomimpact.bancus.COMPLETE_SAVE_AND_EXIT"
        const val Broadcast_SYNC_APPLICATION_UPDATE = "com.bloomimpact.bancus.SYNC_APPLICATION_UPDATE"
        const val Broadcast_COMPLETE_APPLICATION_UPDATE = "com.bloomimpact.bancus.COMPLETE_APPLICATION_UPDATE"

        //General
        const val PRODUCT_TYPE_KEY = "product_type_key"
        const val SECTION = "section"
        const val STATE = "state"
        const val NAME = "name"
        const val COUNTRY = "country"
        const val DOB = "dob"
        const val GENDER = "gender"
        const val COUNTRY_CODE = "country_code"
        const val PHONE_NUMBER = "phone_number"
        const val REFERRAL_CODE = "referral_code"
        const val PIN_CODE = "pin_code"
        const val POSITION = "position"
        const val FROM_BACKGROUND = "from_background"
        const val APPLICATIONS = "applications"
        const val RESPONSES = "responses"
        const val INITIAL_PRODUCT_TYPES = "initial_producttypes"
        const val FINAL_PRODUCT_TYPES = "final_producttypes"
        const val INITIAL_PRODUCTS = "initial_products"
        const val FINAL_PRODUCTS = "final_products"
        const val QUESTION = "question"
        const val QUESTION_ID = "question_id"
        const val QUESTIONS = "questions"
        const val ORDER = "order"
        const val ACTION = "action"
        const val NEXT_QUESTION = "next_question"
        const val PREV_QUESTION = "prev_question"
        const val APPLICATION = "application"
        const val ID = "id"
        const val APPLICATION_ID = "application_id"
        const val APPLICATION_ERRORS = "errors"
        const val TAB_HOME_PRODUCT_APPLICATIONS = 1
        const val TAB_HOME_ACTIVE_APPLICATION = 2
        const val TAB_HOME_ACTIVE_APPLICATION_PROGRESS = 3
        const val TAB_CREDIT_REPORT_MENU = 4
        const val PAGE = "page"
        const val IS_BACK = "is_back"
        const val SHOW = "show"
        const val TITLE = "title"
        const val UPLOAD_FROM_CAMERA_REQUEST = 100
        const val UPLOAD_FROM_GALLERY_REQUEST = 200
        const val SAVE_AND_EXIT = "save_and_exit"
        const val AFTER_DELETE = "after_delete"
        const val IN_PROGRESS = "in_progress"
    }
}