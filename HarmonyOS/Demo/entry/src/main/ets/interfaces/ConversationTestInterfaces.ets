import TestResult from "../pages/ConversationManagerTestPage";
import {
  V2TIMConversationListener,
  V2TIMConversation,
  V2TIMManager,
  V2TIMConversationResult,
  V2TIMConversationType,
  V2TIMConversationListFilter,
  V2TIMConversationOperationResult,
  V2TIMConversationMarkType,
  V2TIMConversationManager,
} from "@tencentcloud/imsdk";
import Prompt from "@system.prompt";
import buffer from "@ohos.buffer";

import { Error } from "./BaseTestInterfaces";
import { writeResult, onCallback, stringifyV2TIMConversation } from "../pages/ConversationManagerTestPage";

export class ConversationTestInterfaces {
  static addConversationListener() {
    V2TIMManager.getConversationManager().addConversationListener(conversationListener);
    Prompt.showToast({ message: "addConversationListener" });
  }

  static removeConversationListener() {
    V2TIMManager.getConversationManager().removeConversationListener(conversationListener);
    Prompt.showToast({ message: "removeConversationListener" });
  }

  static getConversationListByFilter(nextSeq: number, count: number) {
    V2TIMManager.getConversationManager()
      .getConversationListByFilter(nextSeq, count)
      .then((result: V2TIMConversationResult) => {
        writeResult("getConversationListByFilter succeeded", result.nextSeq, result.isFinished, stringifyV2TIMConversation(result.conversationList));
      })
      .catch((error: Error) => {
        writeResult("getConversationListByFilter failed", error.code, error.message);
      });
  }

  static getConversation(conversationID: string) {
    V2TIMManager.getConversationManager()
      .getConversation(conversationID)
      .then((conversation: V2TIMConversation) => {
        writeResult("getConversation succeeded", stringifyV2TIMConversation([conversation]));
      })
      .catch((error: Error) => {
        writeResult("getConversation failed", error.code, error.message);
      });
  }

  static setConversationDraft(conversationID: string, draftText: string) {
    V2TIMManager.getConversationManager()
      .setConversationDraft(conversationID, draftText)
      .then(() => {
        writeResult("setConversationDraft succeeded");
      })
      .catch((error: Error) => {
        writeResult("setConversationDraft failed", error.code, error.message);
      });
  }

  static deleteConversation(conversationID: string) {
    V2TIMManager.getConversationManager()
      .deleteConversation(conversationID)
      .then(() => {
        writeResult("deleteConversation succeeded");
      })
      .catch((error: Error) => {
        writeResult("deleteConversation failed", error.code, error.message);
      });
  }

  static getConversationList(...conversationIDList: string[]) {
    V2TIMManager.getConversationManager()
      .getConversations(conversationIDList)
      .then((conversationList: V2TIMConversation[]) => {
        writeResult("getConversationList succeeded", stringifyV2TIMConversation(conversationList));
      })
      .catch((error: Error) => {
        writeResult("getConversationList failed", error.code, error.message);
      });
  }

  static deleteConversationList(conversationIDList: string[], clearMessage: boolean) {
    V2TIMManager.getConversationManager()
      .deleteConversationList(conversationIDList, clearMessage)
      .then(() => {
        writeResult("deleteConversationList succeeded");
      })
      .catch((error: Error) => {
        writeResult("deleteConversationList failed", error.code, error.message);
      });
  }

  static getConversationListByFilter2(
    nextSeq: number,
    count: number,
    type: V2TIMConversationType,
    conversationType: string,
    markType: number
  ) {
    const info: V2TIMConversationListFilter = {
      type: type,
      conversationGroup: conversationType,
      markType: markType,
    };
    V2TIMManager.getConversationManager()
      .getConversationListByFilter(nextSeq, count, info)
      .then((result: V2TIMConversationResult) => {
        writeResult("getConversationListByFilter2 succeeded", result.nextSeq, result.nextSeq, stringifyV2TIMConversation(result.conversationList));
      })
      .catch((error: Error) => {
        writeResult("getConversationListByFilter2 failed", error.code, error.message);
      });
  }

  static setConversationCustomData(customBuffer: string, ...conversations: string[]) {
    const customData = buffer.from(customBuffer).buffer;
    V2TIMManager.getConversationManager()
      .setConversationCustomData(conversations, customData)
      .then((resultList: V2TIMConversationOperationResult[]) => {
        writeResult("setConversationCustomData succeeded", resultList);
      })
      .catch((error: Error) => {
        writeResult("setConversationCustomData failed", error.code, error.message);
      });
  }

  static pinConversation(conversationID: string, isPinned: boolean) {
    V2TIMManager.getConversationManager()
      .pinConversation(conversationID, isPinned)
      .then(() => {
        writeResult("pinConversation succeeded");
      })
      .catch((error: Error) => {
        writeResult("pinConversation failed", error.code, error.message);
      });
  }

  static markConversation(conversationIDs: string[], markType: number, enableMark: boolean) {
    V2TIMManager.getConversationManager()
      .markConversation(conversationIDs, markType, enableMark)
      .then((resultList: V2TIMConversationOperationResult[]) => {
        writeResult("markConversation succeeded", resultList);
      })
      .catch((error: Error) => {
        writeResult("markConversation failed", error.code, error.message);
      });
  }

  static getTotalUnreadMessageCount() {
    V2TIMManager.getConversationManager()
      .getTotalUnreadMessageCount()
      .then((count: number) => {
        writeResult("getTotalUnreadMessageCount succeeded", count);
      })
      .catch((error: Error) => {
        writeResult("getTotalUnreadMessageCount failed", error.code, error.message);
      });
  }

  static getUnreadMessageCountByFilter(conversationGroup: string, markType: number, type: V2TIMConversationType) {
    const info: V2TIMConversationListFilter = {
      type: type,
      conversationGroup: conversationGroup,
      markType: markType,
    };
    V2TIMManager.getConversationManager()
      .getUnreadMessageCountByFilter(info)
      .then((count: number) => {
        writeResult("getUnreadMessageCountByFilter succeeded", count);
      })
      .catch((error: Error) => {
        writeResult("getUnreadMessageCountByFilter failed", error.code, error.message);
      });
  }

  static subscribeUnreadMessageCountByFilter(conversationGroup: string, markType: number, type: V2TIMConversationType) {
    const info: V2TIMConversationListFilter = {
      type: type,
      conversationGroup: conversationGroup,
      markType: markType,
    };
    V2TIMManager.getConversationManager().subscribeUnreadMessageCountByFilter(info);
    Prompt.showToast({ message: "subscribeUnreadMessageCountByFilter" });
  }

  static unsubscribeUnreadMessageCountByFilter(
    conversationGroup: string,
    markType: number,
    type: V2TIMConversationType
  ) {
    const info: V2TIMConversationListFilter = {
      type: type,
      conversationGroup: conversationGroup,
      markType: markType,
    };
    V2TIMManager.getConversationManager().unsubscribeUnreadMessageCountByFilter(info);
    Prompt.showToast({ message: "unsubscribeUnreadMessageCountByFilter" });
  }

  static cleanConversationUnreadMessageCount(conversationID: string, cleanTimestamp: number, cleanSequence: number) {
    V2TIMManager.getConversationManager()
      .cleanConversationUnreadMessageCount(conversationID, {
        cleanTimestamp: cleanTimestamp,
        cleanSequence: cleanSequence,
      })
      .then(() => {
        writeResult("cleanConversationUnreadMessageCount succeeded");
      })
      .catch((error: Error) => {
        writeResult("cleanConversationUnreadMessageCount failed", error.code, error.message);
      });
  }

  static createConversationGroup(groupName: string, conversationID1: string, conversationID2: string) {
    V2TIMManager.getConversationManager()
      .createConversationGroup(groupName, [conversationID1, conversationID2])
      .then((resultList: V2TIMConversationOperationResult[]) => {
        writeResult("createConversationGroup succeeded", resultList);
      })
      .catch((error: Error) => {
        writeResult("createConversationGroup failed", error.code, error.message);
      });
  }

  static getConversationGroupList() {
    V2TIMManager.getConversationManager()
      .getConversationGroupList()
      .then((resultList: string[]) => {
        writeResult("getConversationGroupList succeeded", resultList);
      })
      .catch((error: Error) => {
        writeResult("getConversationGroupList failed", error.code, error.message);
      });
  }

  static deleteConversationGroup(groupName: string) {
    V2TIMManager.getConversationManager()
      .deleteConversationGroup(groupName)
      .then(() => {
        writeResult("deleteConversationGroup succeeded");
      })
      .catch((error: Error) => {
        writeResult("deleteConversationGroup failed", error.code, error.message);
      });
  }

  static renameConversationGroup(oldName: string, newName: string) {
    V2TIMManager.getConversationManager()
      .renameConversationGroup(oldName, newName)
      .then(() => {
        writeResult("renameConversationGroup succeeded");
      })
      .catch((error: Error) => {
        writeResult("renameConversationGroup failed", error.code, error.message);
      });
  }

  static addConversationsToGroup(groupName: string, conversationID1: string, conversationID2: string) {
    V2TIMManager.getConversationManager()
      .addConversationsToGroup(groupName, [conversationID1, conversationID2])
      .then((resultList: V2TIMConversationOperationResult[]) => {
        writeResult("addConversationsToGroup succeeded", resultList);
      })
      .catch((error: Error) => {
        writeResult("addConversationsToGroup failed", error.code, error.message);
      });
  }

  static deleteConversationsFromGroup(groupName: string, conversationID1: string, conversationID2: string) {
    V2TIMManager.getConversationManager()
      .deleteConversationsFromGroup(groupName, [conversationID1, conversationID2])
      .then((resultList: V2TIMConversationOperationResult[]) => {
        writeResult("deleteConversationsFromGroup succeeded", resultList);
      })
      .catch((error: Error) => {
        writeResult("deleteConversationsFromGroup failed", error.code, error.message);
      });
  }

  static async testConversationGroupList() {
    try {
      await V2TIMManager.getConversationManager().createConversationGroup("testConversationGroupList", ["c2c_haha"]);
      await V2TIMManager.getConversationManager().createConversationGroup("testConversationGroupList2", ["c2c_haha"]);
      const conversation = await V2TIMManager.getConversationManager().getConversation("c2c_haha");
      writeResult(conversation.conversationGroupList);
    } catch (error) {
      writeResult(error);
    }
  }
}

const conversationListener: V2TIMConversationListener = {
  onSyncServerStart: () => {
    onCallback("onSyncServerStart");
  },
  onSyncServerFinish: () => {
    onCallback("onSyncServerFinish");
  },
  onSyncServerFailed: () => {
    onCallback("onSyncServerFailed");
  },
  onNewConversation: (conversationList: V2TIMConversation[]) => {
    onCallback("onNewConversation", stringifyV2TIMConversation(conversationList));
  },
  onConversationChanged: (conversationList: V2TIMConversation[]) => {
    onCallback("onConversationChanged", stringifyV2TIMConversation(conversationList));
  },
  onConversationDeleted: (conversationIDList: string[]) => {
    onCallback("onConversationDeleted", conversationIDList);
  },
  onTotalUnreadMessageCountChanged: (totalUnreadCount: number) => {
    onCallback("onTotalUnreadMessageCountChanged", totalUnreadCount);
  },
};
